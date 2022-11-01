package el.ka.someapp.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.User
import kotlinx.coroutines.tasks.asDeferred

object UsersDatabaseService {
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

  private const val EMAIL_FIELD = "email"
}