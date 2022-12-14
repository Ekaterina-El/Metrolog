package el.ka.someapp.data.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import el.ka.someapp.data.model.*
import el.ka.someapp.data.repository.FirebaseServices.changeField
import el.ka.someapp.data.repository.FirebaseServices.changeFieldArray
import el.ka.someapp.data.repository.FirebaseServices.getDocumentById
import el.ka.someapp.data.repository.UsersDatabaseService.loadCompanyAllUsers
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll

object NodesDatabaseService {
  // region New Node
  fun saveNode(
    node: Node,
    onSuccess: (String) -> Unit = {},
    onFailure: () -> Unit = {}
  ) {

    FirebaseServices.databaseNodes.add(node).addOnFailureListener { onFailure() }
      .addOnSuccessListener { newNode ->
        val userUid = AuthenticationService.getUserUid()!!
        val nodeId = newNode.id
        UsersDatabaseService.addAvailabilityNodes(userUid, nodeId, onFailure) {
          afterAddNode(
            doc = newNode,
            node = node,
            onSuccess = {
              if (node.level == 0) {
                UsersDatabaseService.addToUserAllowedProjects(
                  uid = userUid,
                  nodeId = nodeId,
                  onFailure = { onFailure() },
                  onSuccess = {
                    onSuccess(nodeId)
                  })
              } else onSuccess(nodeId)
            },
            onFailure = onFailure
          )
        }
      }
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
  // endregion

  // region Get Node Info
  fun getNodeById(
    nodeId: String, onSuccess: (Node) -> Unit, onFailure: (ErrorApp) -> Unit
  ) {
    FirebaseServices.databaseNodes.document(nodeId).get()
      .addOnFailureListener {
        onFailure(Errors.somethingWrong)
      }
      .addOnSuccessListener { snap ->
        val doc = snap.toObject(Node::class.java)
        onSuccess(doc!!)
      }
  }

  suspend fun getNodeByID(nodeId: String): DocumentSnapshot =
    FirebaseServices.getDocumentById(nodeId, FirebaseServices.databaseNodes)


  fun getNodesByIDs(nodeIds: List<String>): List<Deferred<DocumentSnapshot>> =
    FirebaseServices.getDocumentsByIDs(nodeIds, collectionRef = FirebaseServices.databaseNodes)

  suspend fun loadCompaniesJobsUsers(jobs: List<JobField>): List<LocalUser> {
    val usersIds = jobs.map { it.userId }
    val usersProfiles = hashMapOf<String, User>()
    loadCompanyAllUsers(usersIds)
      .awaitAll()
      .map { it.toObject(User::class.java)!! }
      .forEach { usersProfiles[it.uid] = it }

    val localUsers = jobs.map {
      LocalUser(
        user = usersProfiles[it.userId]!!,
        jobField = it
      )
    }
    return localUsers
  }
  // endregion

  // region Edit Node / Nodes
  fun changeNodeName(
    nodeId: String, newName: String, onFailure: (ErrorApp) -> Unit, onSuccess: () -> Unit
  ) {
    val ref = FirebaseServices.databaseNodes.document(nodeId)
    changeField(
      ref,
      NODE_NAME_FIELD,
      newName,
      onFailure = { onFailure(Errors.somethingWrong) },
      onSuccess
    )
  }

  private fun addUserIDToAllowedForProject(
    userId: String,
    nodeId: String,
    onFailure: (ErrorApp) -> Unit,
    onSuccess: () -> Unit
  ) {
    val ref = FirebaseServices.databaseNodes.document(nodeId)
    changeFieldArray(
      ref,
      isAdding = true,
      field = USERS_HAVE_ACCESS,
      userId,
      onSuccess,
      onFailure = { onFailure(Errors.somethingWrong) })
  }

  fun denyAccessUserToProject(
    rootNodeId: String,
    userId: String,
    onFailure: () -> Unit,
    onSuccess: () -> Unit
  ) {
    val ref = FirebaseServices.databaseNodes.document(rootNodeId)
    changeFieldArray(
      ref,
      isAdding = false,
      field = USERS_HAVE_ACCESS,
      userId,
      onSuccess,
      onFailure
    )
  }

  fun addUserToProjectByEmail(
    email: String,
    currentProjectId: String,
    onFailure: (ErrorApp) -> Unit,
    onSuccess: (User) -> Unit
  ) {
    // ???????? ???????????????????????? ???? Email
    UsersDatabaseService.getUserByEmail(
      email,
      onFailure = { onFailure(it) },        // ???????????? ???????? ???????????????????????? ???? ????????????
      onSuccess = { user ->
        // ?????????????????? ?????????? ???? ???????????? ???????????????????????? ?? ??????????????. ?????????? -> ????????????
        if (user.allowedProjects.contains(currentProjectId)) onFailure(Errors.userAlreadyHasAccess)
        else {
          // ?????????????????? ???????????????????????? ???????????? ?? ????????????????
          UsersDatabaseService.addToUserAllowedProjects(
            nodeId = currentProjectId,
            uid = user.uid,
            onFailure = { onFailure(Errors.somethingWrong) },
            onSuccess = {
              // ???????????? ?? ???????????????????????? ?????????????????? ?? ????????????
              addUserIDToAllowedForProject(
                userId = user.uid,
                nodeId = currentProjectId,
                onFailure = { onFailure(Errors.somethingWrong) },
                onSuccess = { onSuccess(user) }
              )
            })
        }
      }
    )
  }

  fun addJobField(
    nodeId: String,
    jobField: JobField,
    onFailure: () -> Unit = {},
    onSuccess: () -> Unit
  ) {
    val userId = jobField.userId
    val ref = FirebaseServices.databaseNodes.document(nodeId)
    changeFieldArray(ref, isAdding = true, field = JOBS_FIELD, jobField, onSuccess = {
      if (userId != "" && jobField.jobRole != UserRole.READER) {
        UsersDatabaseService.addAvailabilityNodes(userId, nodeId, onFailure, onSuccess)
      } else onSuccess()
    }, onFailure)
  }

  fun deleteJobField(
    nodeId: String,
    jobField: JobField,
    onFailure: () -> Unit = {},
    onSuccess: () -> Unit = {}
  ) {
    val ref = FirebaseServices.databaseNodes.document(nodeId)
    changeFieldArray(ref, isAdding = false, field = JOBS_FIELD, jobField, onSuccess, onFailure)
  }

  fun addMeasuringId(
    nodeId: String,
    measuringId: String,
    onFailure: () -> Unit,
    onSuccess: () -> Unit
  ) {
    val ref = FirebaseServices.databaseNodes.document(nodeId)
    changeFieldArray(
      ref,
      isAdding = true,
      field = MEASURING_FIELD,
      measuringId,
      onSuccess,
      onFailure
    )
  }

  fun editJobField(
    nodeId: String,
    oldJobField: JobField,
    jobField: JobField,
    onFailure: () -> Unit,
    onSuccess: () -> Unit
  ) {
    deleteJobField(nodeId, oldJobField, onFailure) {
      addJobField(nodeId, jobField, onFailure, onSuccess)
    }
  }

  fun deleteNode(node: Node, onFailure: () -> Unit = {}, onSuccess: () -> Unit = {}) {
    // ???????????????? ?????????????????? node ??????????????????
    node.children.forEach { it ->
      getNodeById(nodeId = it, onFailure = {}, onSuccess = { node ->
        deleteNode(node)
      })
    }

    // ???????????????? ???? ????????
    node.measuring.forEach { MeasuringDatabaseService.deleteMeasuring(measuringId = it) }

    // ???????????????? ???????????????? node
    FirebaseServices
      .databaseNodes
      .document(node.id)
      .delete()
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener {}

    // ???????? nodeLevel = 0
    if (node.level == 0) {
      // ?????????????? ?? ???????? ?????????????????????????? id ??????????????
      node.children.forEach {
        UsersDatabaseService.denyAccessUserToProject(
          nodeId = node.id,
          uid = it,
          onFailure, onSuccess = {})
      }
      onSuccess()
    } else {
      // ?? rootNode ?????????????? ???? children id ???????????????? node
      deleteChildrenFromNode(nodeId = node.rootNodeId!!, childrenId = node.id, onFailure, onSuccess)
    }
  }

  private fun deleteChildrenFromNode(
    nodeId: String,
    childrenId: String,
    onFailure: () -> Unit = {},
    onSuccess: () -> Unit = {}
  ) {
    val ref = FirebaseServices.databaseNodes.document(nodeId)
    changeFieldArray(
      ref,
      isAdding = false,
      CHILDREN_FIELD,
      childrenId,
      onSuccess,
      onFailure
    )
  }

  fun deleteMeasuringIdFromNode(
    locationNodeId: String,
    measuringId: String,
    onFailure: () -> Unit,
    onSuccess: () -> Unit
  ) {
    val ref = FirebaseServices.databaseNodes.document(locationNodeId)
    changeFieldArray(ref, isAdding = false, MEASURING_FIELD, measuringId, onSuccess, onFailure)
  }

  suspend fun getCompanyOfNode(id: String): String {
    val ref = FirebaseServices.databaseNodes
    val node = getDocumentById(id, ref).toObject(Node::class.java)
    return if (node!!.level == 0) node.name else getCompanyOfNode(node.rootNodeId!!)
  }
  // endregion

  private const val LEVEL_FIELD = "level"
  private const val MEASURING_FIELD = "measuring"
  private const val JOBS_FIELD = "jobs"
  private const val NODE_NAME_FIELD = "name"
  private const val ROOT_FIELD = "rootNodeId"
  private const val ID_FIELD = "id"
  private const val CHILDREN_FIELD = "children"
  private const val USERS_HAVE_ACCESS = "usersHaveAccess"
}