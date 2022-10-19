package el.ka.someapp.data.repository

import el.ka.someapp.data.model.*

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
      .addOnSuccessListener { onSuccess() }
      .addOnFailureListener { onFailure() }
  }

  fun getNodesById(
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
    val a = userRole.roleName
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

  private const val LEVEL_FIELD = "level"
}