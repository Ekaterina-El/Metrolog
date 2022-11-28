package el.ka.someapp.data.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import el.ka.someapp.data.model.*
import el.ka.someapp.data.repository.FirebaseServices.changeField
import el.ka.someapp.data.repository.FirebaseServices.changeFieldArray
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
    val ref = FirebaseServices.databaseNodes.document(nodeId)
    changeFieldArray(ref, isAdding = true, field = JOBS_FIELD, jobField, onSuccess, onFailure)
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
    // удаление вложенных node рекурсией
    node.children.forEach { it ->
      getNodeById(nodeId = it, onFailure = {}, onSuccess = { node ->
        deleteNode(node)
      })
    }

    // удаление СИ узла
    node.measuring.forEach { MeasuringDatabaseService.deleteMeasuring(measuringId = it) }

    // Удаление текушего node
    FirebaseServices
      .databaseNodes
      .document(node.id)
      .delete()
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener {}

    // Если nodeLevel = 0
    if (node.level == 0) {
      // Удалить у всех пользователей id проекта
      node.children.forEach {
        UsersDatabaseService.denyAccessUserToProject(
          nodeId = node.id,
          uid = it,
          onFailure, onSuccess = {})
      }
      onSuccess()
    } else {
      // У rootNode удалить из children id текущего node
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

  fun deleteMeasuringIdFromNode(locationNodeId: String, measuringId: String, onFailure: () -> Unit, onSuccess: () -> Unit) {
    val ref = FirebaseServices.databaseNodes.document(locationNodeId)
    changeFieldArray(ref, isAdding = false, MEASURING_FIELD, measuringId, onSuccess, onFailure)
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