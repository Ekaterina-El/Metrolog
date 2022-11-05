package el.ka.someapp.data.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.User
import kotlinx.coroutines.tasks.asDeferred

object UsersDatabaseService {
  fun saveUser(
    userData: User, onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}
  ) {
    FirebaseServices.databaseUsers.document(userData.uid).set(userData)
      .addOnSuccessListener { onSuccess() }.addOnFailureListener { onFailure() }
  }

  fun addToUserAllowedProjects(
    nodeId: String,
    uid: String,
    onFailure: () -> Unit,
    onSuccess: () -> Unit
  ) {
    FirebaseServices
      .databaseUsers
      .document(uid)
      .update(ALLOWED_PROJECTS, FieldValue.arrayUnion(nodeId))
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener { onSuccess() }
  }

  fun loadCurrentUserProfile(
    onSuccess: (User) -> Unit,
    onFailure: () -> Unit
  ) {
    val uid = AuthenticationService.getUserUid()
    if (uid == null) {
      onFailure()
      return
    }

    FirebaseServices
      .databaseUsers
      .document(uid)
      .get()
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener { onSuccess(it.toObject(User::class.java)!!) }
  }

  fun loadCompanyAllUsers(
    listUsersID: List<String>
  ): List<kotlinx.coroutines.Deferred<DocumentSnapshot>> =
    listUsersID.map { userId ->
      FirebaseServices.databaseUsers.document(userId).get().asDeferred()
    }

  fun getUserByEmail(
    email: String,
    onFailure: (ErrorApp) -> Unit,
    onSuccess: (User) -> Unit
  ) {
    FirebaseServices.databaseUsers
      .whereEqualTo(EMAIL_FIELD, email)
      .limit(1L)
      .get()
      .addOnFailureListener { onFailure(Errors.noFoundUser) }
      .addOnSuccessListener {
        if (it.documents.size == 0) onFailure(Errors.noFoundUser)
        else {
          val user = it.documents[0].toObject(User::class.java)!!
          onSuccess(user)
        }
      }
  }

  fun changeProfileImage(uri: Uri, onFailure: () -> Unit, onSuccess: () -> Unit) {
    val ref = FirebaseServices.usersProfilesStore.child(AuthenticationService.getUserUid()!!)
    ref.putFile(uri)
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener {
        ref.downloadUrl
          .addOnFailureListener { onFailure() }
          .addOnSuccessListener {
            changeUserProfileURL(url = it, onFailure = { onFailure() }, onSuccess = { onSuccess() })
          }
      }
    // в записи пользователя изменить ссылку на новое изображение

  }

  private fun changeUserProfileURL(url: Uri, onFailure: () -> Unit, onSuccess: () -> Unit) {
    FirebaseServices.databaseUsers.document(AuthenticationService.getUserUid()!!)
      .update(PROFILE_IMAGE_URL_FIELD, url)
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener { onSuccess() }
  }

  private const val EMAIL_FIELD = "email"
  private const val PROFILE_IMAGE_URL_FIELD = "profileImageUrl"
  private const val ALLOWED_PROJECTS = "allowedProjects"
}