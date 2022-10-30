package el.ka.someapp.data.repository

import el.ka.someapp.data.model.User

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
}