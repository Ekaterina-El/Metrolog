package el.ka.someapp.data.repository

import com.google.firebase.firestore.DocumentSnapshot
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
}