package el.ka.someapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseServices {
  private const val USERS_COLLECTION = "users"
  private const val NODES_COLLECTION = "nodes"

  val auth = FirebaseAuth.getInstance()

  val databaseUsers = Firebase.firestore.collection(USERS_COLLECTION)
  val databaseNodes = Firebase.firestore.collection(NODES_COLLECTION)
}