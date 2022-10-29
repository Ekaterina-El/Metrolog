package el.ka.someapp.data.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.inject.Deferred
import el.ka.someapp.data.model.*
import kotlinx.coroutines.tasks.asDeferred

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

  fun saveNode(
    node: Node,
    onSuccess: () -> Unit = {},
    onFailure: () -> Unit = {}
  ) {
    FirebaseServices.databaseNodes
      .add(node)
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener {
        afterAddNode(
          doc = it,
          node = node,
          onSuccess = onSuccess,
          onFailure = onFailure
        )
      }
  }

  private fun afterAddNode(
    doc: DocumentReference,
    node: Node,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
  ) {
    doc
      .update(ID_FIELD, doc.id)
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener {
        if (node.rootNodeId != null) {
          addNodeIDToParent(
            rootId = node.rootNodeId,
            nodeId = doc.id,
            onFailure = onFailure,
            onSuccess = onSuccess
          )
        } else {
          onSuccess()
        }
      }
  }

  private fun addNodeIDToParent(
    rootId: String,
    nodeId: String,
    onFailure: () -> Unit,
    onSuccess: () -> Unit
  ) {
    FirebaseServices.databaseNodes.document(rootId)
      .update(CHILDREN_FIELD, FieldValue.arrayUnion(nodeId))
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener { onSuccess() }
  }

  fun checkUniqueNodeName(
    node: Node,
    onFailure: (ErrorApp) -> Unit,
    onSuccess: () -> Unit
  ) {
    var query = FirebaseServices.databaseNodes
      .whereEqualTo(NODE_NAME_FIELD, node.name)
      .whereEqualTo(LEVEL_FIELD, node.level)

    if (node.rootNodeId != null) {
      query = query.whereEqualTo(ROOT_FIELD, node.rootNodeId)
    }

    query.get()
      .addOnSuccessListener {
        if (it.documents.size > 0) onFailure(Errors.nonUniqueName) else onSuccess()
      }
      .addOnFailureListener { onFailure(Errors.somethingWrong) }
  }

  fun getNodeById(
    nodeId: String,
    onSuccess: (Node) -> Unit,
    onFailure: (ErrorApp) -> Unit
  ) {

    FirebaseServices.databaseNodes
      .document(nodeId)
      .get()
      .addOnSuccessListener { snap ->
        val doc = snap.toObject(Node::class.java)
        onSuccess(doc!!)
      }
      .addOnFailureListener {
        onFailure(Errors.somethingWrong)
      }
  }

  suspend fun getNodesInLevelRoot(
    children: List<String>): List<kotlinx.coroutines.Deferred<DocumentSnapshot>> {
    // получаем список children у Root
    // прходим по всем id children
    //    * последовательно загружаем по id узел (getNodeByID)
    //    * после получения узла добавляем его в спиоск
    // вызываем функцию OnSuccess педелаём, в качестве аргумента, список загруженых узлов
    val db = FirebaseServices.databaseNodes
    return children.map { nodeId ->
      db.document(nodeId).get().asDeferred()
    }

    /*
    FirebaseServices
      .databaseNodes
      .whereEqualTo(ROOT_FIELD, root)
      .whereEqualTo(LEVEL_FIELD, level)
      .get()
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener {
        onSuccess(it.toObjects(Node::class.java))
      }
     */
  }


  fun getUserMainNodes(
    userId: String,
    onSuccess: (List<Node>) -> Unit = {},
    onFailure: (ErrorApp) -> Unit = {}
  ) {
    val foundNodes = mutableListOf<Node>()

    getUserMainNodesWithRole(
      userId = userId,
      userRole = UserRole.HEAD,
      onFailure = { onFailure(Errors.somethingWrong) },
      onSuccess = { headNodes ->
        foundNodes.addAll(headNodes)
        getUserMainNodesWithRole(
          userId = userId,
          userRole = UserRole.EDITOR,
          onFailure = { onFailure(Errors.somethingWrong) },
          onSuccess = { editorNodes ->
            foundNodes.addAll(editorNodes)
            getUserMainNodesWithRole(
              userId = userId,
              userRole = UserRole.READER,
              onFailure = { onFailure(Errors.somethingWrong) },
              onSuccess = { readerNodes ->
                foundNodes.addAll(readerNodes)
                onSuccess(foundNodes)
              })
          })
      })
  }

  private fun getUserMainNodesWithRole(
    userId: String,
    userRole: UserRole,
    onSuccess: (Array<Node>) -> Unit,
    onFailure: (ErrorApp) -> Unit
  ) {
    FirebaseServices.databaseNodes
      .whereEqualTo(LEVEL_FIELD, 0)
      .whereArrayContains(userRole.roleName, userId)
      .get()
      .addOnFailureListener { onFailure(Errors.somethingWrong) }
      .addOnSuccessListener {
        val nodes = (it.toObjects(Node::class.java))
        onSuccess(nodes.toTypedArray())
      }
  }

  fun changeNodeName(
    nodeId: String,
    newName: String,
    onFailure: (ErrorApp) -> Unit,
    onSuccess: () -> Unit
  ) {
    FirebaseServices.databaseNodes
      .document(nodeId)
      .update(NODE_NAME_FIELD, newName)
      .addOnFailureListener { onFailure(Errors.somethingWrong) }
      .addOnSuccessListener { onSuccess() }
  }

  private const val LEVEL_FIELD = "level"
  private const val NODE_NAME_FIELD = "name"
  private const val ROOT_FIELD = "rootNodeId"
  private const val ID_FIELD = "id"
  private const val CHILDREN_FIELD = "children"
}