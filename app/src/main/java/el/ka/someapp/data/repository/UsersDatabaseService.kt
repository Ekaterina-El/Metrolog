package el.ka.someapp.data.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.StorageReference
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.User

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
    FirebaseServices.getDocumentsByIDs(listUsersID, FirebaseServices.databaseUsers)

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

  fun changeBackgroundImage(uri: Uri, onFailure: () -> Unit, onSuccess: (String) -> Unit) {
    val ref = FirebaseServices.usersBackgroundsStore.child(AuthenticationService.getUserUid()!!)
    loadImageToStore(ref, uri, onFailure, onSuccess = {
      changeBackgroundProfileURL(url = it, onFailure, onSuccess = { onSuccess(it) })
    })
  }

  fun changeProfileImage(uri: Uri, onFailure: () -> Unit, onSuccess: (String) -> Unit) {
    val ref = FirebaseServices.usersProfilesStore.child(AuthenticationService.getUserUid()!!)
    loadImageToStore(ref, uri, onFailure, onSuccess = {
      changeUserProfileURL(url = it, onFailure, onSuccess = { onSuccess(it) })
    })
  }

  private fun loadImageToStore(
    ref: StorageReference,
    uri: Uri,
    onFailure: () -> Unit,
    onSuccess: (String) -> Unit
  ) {
    ref.putFile(uri)
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener {
        ref.downloadUrl
          .addOnFailureListener { onFailure() }
          .addOnSuccessListener { onSuccess(it.toString()) }
      }
  }

  private fun changeField(field: String, value: Any, onFailure: () -> Unit, onSuccess: () -> Unit) {
    FirebaseServices.databaseUsers
      .document(AuthenticationService.getUserUid()!!)
      .update(field, value)
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener { onSuccess() }
  }

  private fun changeUserProfileURL(url: String, onFailure: () -> Unit, onSuccess: () -> Unit) {
    changeField(field = PROFILE_IMAGE_URL_FIELD, value = url, onFailure, onSuccess)
  }

  private fun changeBackgroundProfileURL(url: String, onFailure: () -> Unit, onSuccess: () -> Unit) {
    changeField(field = BACKGROUND_IMAGE_URL_FIELD, value = url, onFailure, onSuccess)
  }

  fun changeProfileFullName(newFullName: String, onFailure: () -> Unit, onSuccess: () -> Unit) {
    changeField(field = FULL_NAME_FIELD, value = newFullName, onFailure, onSuccess)
  }

  private const val FULL_NAME_FIELD = "fullName"
  private const val EMAIL_FIELD = "email"
  private const val PROFILE_IMAGE_URL_FIELD = "profileImageUrl"
  private const val BACKGROUND_IMAGE_URL_FIELD = "backgroundImageUrl"
  private const val ALLOWED_PROJECTS = "allowedProjects"
}