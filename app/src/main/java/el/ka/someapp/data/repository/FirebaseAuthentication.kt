package el.ka.someapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object FirebaseAuthentication {
  private const val TAG = "FirebaseAuthentication"
  private val auth = FirebaseAuth.getInstance()

  fun registerUser(
    email: String,
    password: String,
    onSuccess: (FirebaseUser) -> Unit = {},
    onFailure: () -> Unit = {}
  ) {
    auth
      .createUserWithEmailAndPassword(email, password)
      .addOnSuccessListener {
        val user = auth.currentUser!!
        user.sendEmailVerification()
        onSuccess(user)
      }
      .addOnFailureListener { onFailure() }
  }

}