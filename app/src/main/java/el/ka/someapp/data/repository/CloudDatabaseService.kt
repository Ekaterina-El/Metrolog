package el.ka.someapp.data.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.Node
import el.ka.someapp.data.model.User
import kotlinx.coroutines.tasks.asDeferred

object CloudDatabaseService {
  fun saveUser(
    userData: User, onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}
  ) {
    FirebaseServices.databaseUsers.document(userData.uid).set(userData)
      .addOnSuccessListener { onSuccess() }.addOnFailureListener { onFailure() }
  }

  fun saveNode(
    node: Node, onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}
  ) {
    FirebaseServices.databaseNodes.add(node).addOnFailureListener { onFailure() }
      .addOnSuccessListener { newNode ->
        afterAddNode(
          doc = newNode,
          node = node,
          onSuccess = {
            if (node.level == 0) {
              addToUsersAllowedProjects(
                nodeId = newNode.id,
                onFailure = { onFailure() },
                onSuccess = { onSuccess() })
            } else onSuccess()
          },
          onFailure = onFailure
        )
      }
  }

  private fun addToUsersAllowedProjects(
    nodeId: String,
    onFailure: () -> Unit,
    onSuccess: () -> Unit
  ) {
    FirebaseServices
      .databaseUsers
      .document(AuthenticationService.getUserUid()!!)
      .update(ALLOWED_PROJECTS, FieldValue.arrayUnion(nodeId))
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener { onSuccess() }
  }

  private fun afterAddNode(
    doc: DocumentReference, node: Node, onSuccess: () -> Unit, onFailure: () -> Unit
  ) {
    doc.update(ID_FIELD, doc.id).addOnFailureListener { onFailure() }.addOnSuccessListener {
      if (node.rootNodeId != null) {
        addNodeIDToParent(
          rootId = node.rootNodeId, nodeId = doc.id, onFailure = onFailure, onSuccess = onSuccess
        )
      } else {
        onSuccess()
      }
    }
  }

  private fun addNodeIDToParent(
    rootId: String, nodeId: String, onFailure: () -> Unit, onSuccess: () -> Unit
  ) {
    FirebaseServices.databaseNodes.document(rootId)
      .update(CHILDREN_FIELD, FieldValue.arrayUnion(nodeId)).addOnFailureListener { onFailure() }
      .addOnSuccessListener { onSuccess() }
  }

  fun checkUniqueNodeName(
    node: Node, onFailure: (ErrorApp) -> Unit, onSuccess: () -> Unit
  ) {
    var query = FirebaseServices.databaseNodes.whereEqualTo(NODE_NAME_FIELD, node.name)
      .whereEqualTo(LEVEL_FIELD, node.level)

    if (node.rootNodeId != null) {
      query = query.whereEqualTo(ROOT_FIELD, node.rootNodeId)
    }

    query.get().addOnSuccessListener {
      if (it.documents.size > 0) onFailure(Errors.nonUniqueName) else onSuccess()
    }.addOnFailureListener { onFailure(Errors.somethingWrong) }
  }

  fun getNodeById(
    nodeId: String, onSuccess: (Node) -> Unit, onFailure: (ErrorApp) -> Unit
  ) {

    FirebaseServices.databaseNodes.document(nodeId).get().addOnSuccessListener { snap ->
      val doc = snap.toObject(Node::class.java)
      onSuccess(doc!!)
    }.addOnFailureListener {
      onFailure(Errors.somethingWrong)
    }
  }

  suspend fun getNodesByIDs(
    nodeIds: List<String>
  ): List<kotlinx.coroutines.Deferred<DocumentSnapshot>> =
    nodeIds.map { nodeId ->
      FirebaseServices.databaseNodes.document(nodeId).get().asDeferred()
    }

  fun changeNodeName(
    nodeId: String, newName: String, onFailure: (ErrorApp) -> Unit, onSuccess: () -> Unit
  ) {
    FirebaseServices.databaseNodes.document(nodeId).update(NODE_NAME_FIELD, newName)
      .addOnFailureListener { onFailure(Errors.somethingWrong) }
      .addOnSuccessListener { onSuccess() }
  }

  private const val LEVEL_FIELD = "level"
  private const val NODE_NAME_FIELD = "name"
  private const val ROOT_FIELD = "rootNodeId"
  private const val ID_FIELD = "id"
  private const val CHILDREN_FIELD = "children"

  private const val ALLOWED_PROJECTS = "allowedProjects"
}