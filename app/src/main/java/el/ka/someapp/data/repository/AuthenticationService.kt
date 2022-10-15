package el.ka.someapp.data.repository

import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.User

object AuthenticationService {
  private const val TAG = "FirebaseAuthentication"
  private val auth = FirebaseAuth.getInstance()
  private val databaseUsers = FirebaseDatabase.getInstance().getReference("users")


  fun registerUser(
    password: String,
    userData: User,
    onSuccess: (FirebaseUser) -> Unit = {},
    onFailure: (ErrorApp) -> Unit = {}
  ) {
    auth
      .createUserWithEmailAndPassword(userData.email, password)
      .addOnSuccessListener {
        val user = auth.currentUser!!
        userData.uid = user.uid


        enterUserDataToRealtimeDatabase(
          userData = userData,
          onSuccess = {
            user.sendEmailVerification()
            onSuccess(user)
          },
          onFailure = {
            onFailure(Errors.somethingWrong)
            user.delete()
          }
        )


      }
      .addOnFailureListener {
        val error = when (it as FirebaseAuthException) {
          is FirebaseAuthWeakPasswordException -> Errors.weakPassword
          is FirebaseAuthInvalidCredentialsException -> Errors.invalidCredentials
          is FirebaseAuthUserCollisionException -> Errors.collisionUser
          else -> Errors.somethingWrong
        }

        onFailure(error)
      }
  }

  private fun enterUserDataToRealtimeDatabase(
    userData: User,
    onSuccess: () -> Unit = {},
    onFailure: () -> Unit = {}
  ) {
    databaseUsers.child(userData.uid).setValue(userData)
      .addOnCompleteListener {
        if (it.isSuccessful) onSuccess() else onFailure()
      }
  }

  fun loginUser(
    email: String,
    password: String,
    onSuccess: () -> Unit,
    onFailed: () -> Unit
  ) {
    auth
      .signInWithEmailAndPassword(email, password)
      .addOnSuccessListener { onSuccess() }
      .addOnFailureListener { onFailed() }
  }

}