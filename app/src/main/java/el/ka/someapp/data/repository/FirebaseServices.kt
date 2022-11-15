package el.ka.someapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.tasks.asDeferred

object FirebaseServices {
  val auth = FirebaseAuth.getInstance()

  private const val USERS_COLLECTION = "users"
  val databaseUsers = Firebase.firestore.collection(USERS_COLLECTION)

  private const val NODES_COLLECTION = "nodes"
  val databaseNodes = Firebase.firestore.collection(NODES_COLLECTION)

  private const val MEASURING_COLLECTION = "measuring"
  val databaseMeasuring = Firebase.firestore.collection(MEASURING_COLLECTION)

  private val storage = FirebaseStorage.getInstance()

  private const val USERS_PROFILES_COLLECTION = "users_profiles"
  val usersProfilesStore = storage.getReference(USERS_PROFILES_COLLECTION)

  private const val USERS_BACKGROUND = "users_background"
  val usersBackgroundsStore = storage.getReference(USERS_BACKGROUND)


  fun getDocumentsByIDs(docNodes: List<String>, collectionRef: CollectionReference): List<Deferred<DocumentSnapshot>> =
    docNodes.map { docId ->
      collectionRef
        .document(docId)
        .get()
        .asDeferred()
    }
}