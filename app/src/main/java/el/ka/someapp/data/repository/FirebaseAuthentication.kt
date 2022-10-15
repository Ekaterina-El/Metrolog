package el.ka.someapp.data.repository

import com.google.firebase.auth.*
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.Errors

object FirebaseAuthentication {
  private const val TAG = "FirebaseAuthentication"
  private val auth = FirebaseAuth.getInstance()

  fun registerUser(
    email: String,
    password: String,
    onSuccess: (FirebaseUser) -> Unit = {},
    onFailure: (ErrorApp) -> Unit = {}
  ) {
    auth
      .createUserWithEmailAndPassword(email, password)
      .addOnSuccessListener {
        val user = auth.currentUser!!
        user.sendEmailVerification()
        onSuccess(user)
      }
      .addOnFailureListener {
        val error = when(it as FirebaseAuthException) {
          is FirebaseAuthWeakPasswordException -> Errors.weakPassword
          is FirebaseAuthInvalidCredentialsException -> Errors.invalidCredentials
          is FirebaseAuthUserCollisionException -> Errors.collisionUser
          else -> Errors.somethingWrong
        }

        onFailure(error)
      }
  }

}