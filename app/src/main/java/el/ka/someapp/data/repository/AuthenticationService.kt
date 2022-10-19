package el.ka.someapp.data.repository

import com.google.firebase.auth.*
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.User
import el.ka.someapp.data.repository.FirebaseServices.auth

object AuthenticationService {
  private const val TAG = "FirebaseAuthentication"

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

        CloudDatabaseService.saveUser(
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

  fun loginUser(
    email: String,
    password: String,
    onSuccess: () -> Unit = {},
    onFailed: (ErrorApp) -> Unit = {}
  ) {
    auth
      .signInWithEmailAndPassword(email, password)
      .addOnSuccessListener {
        val user = auth.currentUser
        if (user!!.isEmailVerified) {
          onSuccess()
        } else {
          val error = Errors.noVerifiedEmail
          onFailed(error)
          user.sendEmailVerification()
          auth.signOut()
        }
      }
      .addOnFailureListener {
        val error = when (it as FirebaseAuthException) {
          is FirebaseAuthInvalidUserException -> Errors.invalidUser
          is FirebaseAuthInvalidCredentialsException -> Errors.invalidEmailOrPassword
          else -> Errors.somethingWrong
        }

        onFailed(error)
      }
  }

  fun checkUserIsAuth(onAuth: () -> Unit, onNoAuth: () -> Unit) {

    if (auth.currentUser != null) {
      // region TestCode
/*
      val node = Node(
        name = "Предприятие №1",
        level = 1,
        head = listOf("sdadsa3"),
        rootNodeId = "tB7vfk0O9VWsp69QnGfD"
      )
      CloudDatabaseService.saveNode(
        node = node,
        onSuccess = {
          val a = 10
        },
        onFailure = {
          val a = 10
        })


 */
      /*
      CloudDatabaseService.getUserMainNodes(
        userId = auth.currentUser!!.uid,
        onSuccess = { nodes ->
        },
        onFailure = { error ->
        }
      )*/
      //endregion

      onAuth()
    } else onNoAuth()
  }

  fun logout() {
    auth.signOut()
  }

  fun resetPassword(email: String, onSuccess: () -> Unit, onFailure: (ErrorApp) -> Unit) {
    auth.sendPasswordResetEmail(email)
      .addOnSuccessListener { onSuccess() }
      .addOnFailureListener {
        val error = when (it as FirebaseAuthException) {
          is FirebaseAuthInvalidUserException -> Errors.noFoundUser
          else -> Errors.somethingWrong
        }
        onFailure(error)
      }
  }
}