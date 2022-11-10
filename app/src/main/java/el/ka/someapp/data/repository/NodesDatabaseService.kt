package el.ka.someapp.data.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import el.ka.someapp.data.model.*
import el.ka.someapp.data.repository.UsersDatabaseService.loadCompanyAllUsers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.asDeferred

object NodesDatabaseService {
  // region New Node
  fun saveNode(
    node: Node,
    onSuccess: (String) -> Unit = {},
    onFailure: () -> Unit = {}
  ) {
    FirebaseServices.databaseNodes.add(node).addOnFailureListener { onFailure() }
      .addOnSuccessListener { newNode ->
        afterAddNode(
          doc = newNode,
          node = node,
          onSuccess = {
            if (node.level == 0) {
              UsersDatabaseService.addToUserAllowedProjects(
                uid = AuthenticationService.getUserUid()!!,
                nodeId = newNode.id,
                onFailure = { onFailure() },
                onSuccess = { onSuccess(newNode.id) })
            } else onSuccess(newNode.id)
          },
          onFailure = onFailure
        )
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
    FirebaseServices.databaseNodes.document(nodeId).get().addOnSuccessListener { snap ->
      val doc = snap.toObject(Node::class.java)
      onSuccess(doc!!)
    }.addOnFailureListener {
      onFailure(Errors.somethingWrong)
    }
  }

  fun getNodesByIDs(
    nodeIds: List<String>
  ): List<kotlinx.coroutines.Deferred<DocumentSnapshot>> =
    nodeIds.map { nodeId ->
      FirebaseServices.databaseNodes.document(nodeId).get().asDeferred()
    }

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
    FirebaseServices.databaseNodes.document(nodeId).update(NODE_NAME_FIELD, newName)
      .addOnFailureListener { onFailure(Errors.somethingWrong) }
      .addOnSuccessListener { onSuccess() }
  }

  private fun addUserIDToAllowedForProject(
    userId: String,
    nodeId: String,
    onFailure: (ErrorApp) -> Unit,
    onSuccess: () -> Unit
  ) {
    FirebaseServices.databaseNodes.document(nodeId)
      .update(USERS_HAVE_ACCESS, FieldValue.arrayUnion(userId))
      .addOnFailureListener { onFailure(Errors.somethingWrong) }
      .addOnSuccessListener { onSuccess() }
  }

  fun addUserToProjectByEmail(
    email: String,
    currentProjectId: String,
    onFailure: (ErrorApp) -> Unit,
    onSuccess: (User) -> Unit
  ) {
    // Ищем пользователя по Email
    UsersDatabaseService.getUserByEmail(
      email,
      onFailure = { onFailure(it) },        // Ошибка если пользователь не найлен
      onSuccess = { user ->
        // Проверяем имеет ли доступ пользователь к проекту. Имеет -> ошибка
        if (user.allowedProjects.contains(currentProjectId)) onFailure(Errors.userAlreadyHasAccess)
        else {
          // Разрешаем пользователю доступ к проектку
          UsersDatabaseService.addToUserAllowedProjects(
            nodeId = currentProjectId,
            uid = user.uid,
            onFailure = { onFailure(Errors.somethingWrong) },
            onSuccess = {
              // Данные о пользователе добавляем в проект
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
    FirebaseServices.databaseNodes.document(nodeId)
      .update(JOBS_FIELD, FieldValue.arrayUnion(jobField))
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener { onSuccess() }
  }

  fun addMeasuringId(
    nodeId: String,
    measuringId: String,
    onFailure: () -> Unit,
    onSuccess: () -> Unit) {
    FirebaseServices
      .databaseNodes
      .document(nodeId)
      .update(MEASURING_FIELD, FieldValue.arrayUnion(measuringId))
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener { onSuccess() }
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