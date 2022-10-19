package el.ka.someapp.data.repository

import el.ka.someapp.data.model.User

object CloudDatabaseService {
  fun saveUser(
    userData: User,
    onSuccess: () -> Unit = {},
    onFailure: () -> Unit = {}
  ) {
    FirebaseServices.databaseUsers
      .document(userData.uid)
      .set(userData)
      .addOnSuccessListener { onSuccess() }
      .addOnFailureListener { onFailure() }
  }
}