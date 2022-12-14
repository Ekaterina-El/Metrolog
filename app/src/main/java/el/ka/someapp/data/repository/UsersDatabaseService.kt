package el.ka.someapp.data.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.StorageReference
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.User
import el.ka.someapp.data.repository.FirebaseServices.changeField
import el.ka.someapp.data.repository.FirebaseServices.changeFieldArray

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
    val ref = FirebaseServices.databaseUsers.document(uid)
    changeFieldArray(ref, isAdding = true, field = ALLOWED_PROJECTS, nodeId, onSuccess, onFailure)
  }

  fun denyAccessUserToProject(
    nodeId: String,
    uid: String,
    onFailure: () -> Unit,
    onSuccess: () -> Unit
  ) {
    val ref = FirebaseServices.databaseUsers.document(uid)
    changeFieldArray(ref, isAdding = false, field = ALLOWED_PROJECTS, nodeId, onSuccess, onFailure)
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
      .addOnFailureListener {
        onFailure()
      }
      .addOnSuccessListener {
        onSuccess(it.toObject(User::class.java)!!)
      }
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

  suspend fun getUserByUid(uid: String): DocumentSnapshot =
    FirebaseServices.getDocumentById(uid, ref = FirebaseServices.databaseUsers)


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

  private fun changeUserProfileURL(url: String, onFailure: () -> Unit, onSuccess: () -> Unit) {
    val ref = FirebaseServices.databaseUsers.document(AuthenticationService.getUserUid()!!)
    changeField(ref, field = PROFILE_IMAGE_URL_FIELD, value = url, onFailure, onSuccess)
  }

  private fun changeBackgroundProfileURL(
    url: String,
    onFailure: () -> Unit,
    onSuccess: () -> Unit
  ) {
    val ref = FirebaseServices.databaseUsers.document(AuthenticationService.getUserUid()!!)
    changeField(ref, field = BACKGROUND_IMAGE_URL_FIELD, value = url, onFailure, onSuccess)
  }

  fun changeProfileFullName(newFullName: String, onFailure: () -> Unit, onSuccess: () -> Unit) {
    val ref = FirebaseServices.databaseUsers.document(AuthenticationService.getUserUid()!!)
    changeField(ref, field = FULL_NAME_FIELD, value = newFullName, onFailure, onSuccess)
  }

  private const val FULL_NAME_FIELD = "fullName"
  private const val EMAIL_FIELD = "email"
  private const val PROFILE_IMAGE_URL_FIELD = "profileImageUrl"
  private const val BACKGROUND_IMAGE_URL_FIELD = "backgroundImageUrl"
  private const val ALLOWED_PROJECTS = "allowedProjects"
}