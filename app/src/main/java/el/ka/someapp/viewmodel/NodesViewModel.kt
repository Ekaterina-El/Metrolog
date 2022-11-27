package el.ka.someapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.someapp.data.model.*
import el.ka.someapp.data.model.measuring.*
import el.ka.someapp.data.repository.AuthenticationService
import el.ka.someapp.data.repository.MeasuringDatabaseService
import el.ka.someapp.data.repository.NodesDatabaseService
import el.ka.someapp.data.repository.UsersDatabaseService
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class NodesViewModel(application: Application) : AndroidViewModel(application) {
  private val _loads = MutableLiveData<MutableSet<Int>>(mutableSetOf())
  val loads: LiveData<MutableSet<Int>> get() = _loads

  private fun clearFields() {
    filterFieldMeasuring.value = ""
    _measuring.value = listOf()
    _filteredNodes.value = listOf()
    _nodesHistory.value = listOf()
    _currentNode.value = null
    _nodes.value = listOf()
    _currentMeasuring.value = null
    filter.value = ""
    _filteredNodes.value = listOf()
    _companyAllUsers.value = listOf()
    filterUsersVal.value = ""
    _filteredUsers.value = listOf()
    _localUsers.value = listOf()
    _loads.value
  }

  private val _state = MutableLiveData(State.VIEW)
  val state: LiveData<State>
    get() = _state

  fun toViewState() {
    _state.value = State.VIEW
  }

  // region Loads
  companion object {
    const val LOAD_CURRENT_USER_PROFILE = 1
    const val LOAD_NODES_FROM_DB_BY_ID_LIST = 2
    const val SET_NODES = 3
    const val SAVE_NODE_WITH_CHECK = 4
    const val ADD_NODE = 5
    const val LOAD_NODE_BY_ID = 6
    const val AFTER_LOAD_NODE = 7
    const val LOAD_ALL_COMPANY_USER = 8
    const val LOAD_LOCAL_USERS = 9
    const val DELETE_MEASURING = 10
    const val NAVIGATE_BY_HISTORY_TO = 11
    const val LOAD_MEASURING = 12
    const val SET_MEASURING = 13
    const val FIND_USER_ROLE_IN_CURRENT_NODE = 14
    const val CHANGE_NODE_NAME = 15
    const val DELETE_NODE = 16
    const val SET_USERS = 17
    const val ADD_USER_USER_BY_EMAIL = 18
    const val DENY_ACCESS_USER = 19
  }

  private fun changeLoads(state: Int, isAdding: Boolean = true) {
    val states = _loads.value!!.toMutableSet()
    if (isAdding) states.add(state) else states.remove(state)
    _loads.value = states
  }
  // endregion

  // region Measuring
  val filterFieldMeasuring = MutableLiveData("")

  private val _measuring = MutableLiveData<List<Measuring>>(listOf())

  private val _measuringFiltered = MutableLiveData<List<Measuring>>(listOf())
  val measuringFiltered: LiveData<List<Measuring>> get() = _measuringFiltered

  fun loadMeasuring() {
    changeLoads(LOAD_MEASURING)
//    _state.value = State.LOADING
    viewModelScope.launch {
      val measuringItems = MeasuringDatabaseService
        .getMeasuringByIDs(_currentNode.value!!.measuring)
        .awaitAll()
        .mapNotNull {
          val measuring = it.toObject(Measuring::class.java)
          if (measuring != null) measuring.measuringID = it.id
          measuring
        }
      setMeasuring(measuringItems)
      changeLoads(LOAD_MEASURING, false)
    }
  }

  private fun setMeasuring(measuringItems: List<Measuring>) {
    changeLoads(SET_MEASURING)
    _measuring.value = measuringItems
    filterMeasuring()
    changeLoads(SET_MEASURING, false)
    //    _state.value = State.VIEW
  }

  fun filterMeasuring() {
    val f = filterFieldMeasuring.value!!
    _measuringFiltered.value =
      if (f.isEmpty()) _measuring.value
      else _measuring.value!!.filter {
        return@filter it.passport!!.name.contains(f, true) ||
            it.passport!!.type.contains(f, true) ||
            it.passport!!.inventoryNumber!!.contains(f, true)
      }
  }

  fun addMeasuringToLocal(measuringId: String) {
    val measuringItems = _currentNode.value!!.measuring.toMutableList()
    measuringItems.add(measuringId)
    _currentNode.value!!.measuring = measuringItems
  }

  fun clearFilterMeasuring() {
    filterFieldMeasuring.value = ""
    filterMeasuring()
  }
  // endregion

  // region History
  private val _nodesHistory = MutableLiveData<List<Node>>(listOf())
  val nodesHistory: LiveData<List<Node>>
    get() = _nodesHistory

  private fun getCurrentRootNode() = _nodesHistory.value!![0]

  private fun addToHistory(node: Node) {
    if (_nodesHistory.value!!.isEmpty() || _nodesHistory.value!!.last().id != node.id) {
      val history = _nodesHistory.value!!.toMutableList()
      history.add(node)
      _nodesHistory.value = history.toList()
    }
  }

  private fun updateHistoryItem(node: Node, nodeId: String) {
    _nodesHistory.value = _nodesHistory.value!!.map {
      return@map if (it.id == nodeId) node else it
    }
  }

  private fun popupHistory() {
    val history = _nodesHistory.value!!.toMutableList()
    history.removeLastOrNull()
    _nodesHistory.value = history

    val roles = historyRole.value!!.toMutableList()
    roles.removeLastOrNull()
    historyRole.value = roles

    updateCurrentNodeRole()
  }

  fun goBack() {
    popupHistory()

    if (_nodesHistory.value!!.isEmpty()) {
      clearFields()
      _state.value = State.BACK
    } else {
      loadNodeByID(_nodesHistory.value!!.last().id)
    }
  }

  fun navigateByHistoryTo(node: Node) {
    if (node.id == _currentNode.value!!.id) return
    changeLoads(NAVIGATE_BY_HISTORY_TO)
//    _state.value = State.LOADING

    // получить индекс nodeID
    val idx = _nodesHistory.value!!.indexOf(node)

    // преобрзование списка истории узлов
    _nodesHistory.value = _nodesHistory.value!!.subList(0, idx + 1)
    findUserRoleInCurrentNode(idx + 1)

    // загрузить данные о текущем node
    loadNodeByID(node.id, false)
    changeLoads(NAVIGATE_BY_HISTORY_TO, false)
  }
  // endregion

  // region User
  private val _currentUserProfile = MutableLiveData<User?>(null)
  val currentUserProfile: LiveData<User?>
    get() = _currentUserProfile

  fun loadCurrentUserProfile(onSuccess: () -> Unit = {}) {
    changeLoads(LOAD_CURRENT_USER_PROFILE)
    UsersDatabaseService.loadCurrentUserProfile(
      onFailure = {},
      onSuccess = {
        _currentUserProfile.value = it
        onSuccess()
        changeLoads(LOAD_CURRENT_USER_PROFILE, isAdding = false)
      }
    )
  }

  fun changeProfileImage(uri: Uri) {
    UsersDatabaseService.changeProfileImage(
      uri = uri,
      onFailure = {},
      onSuccess = { newUrl ->
        _currentUserProfile.value!!.profileImageUrl = newUrl
      }
    )
  }

  fun changeBackgroundImage(uri: Uri) {
    UsersDatabaseService.changeBackgroundImage(
      uri = uri,
      onFailure = {},
      onSuccess = { newUrl ->
        _currentUserProfile.value!!.backgroundImageUrl = newUrl
      }
    )
  }

  fun changeProfileFullName(newName: String) {
    UsersDatabaseService.changeProfileFullName(
      newFullName = newName,
      onSuccess = {
        _currentUserProfile.value!!.fullName = newName
      },
      onFailure = {}
    )
  }

  fun logout(afterLogout: () -> Unit = {}) {
    AuthenticationService.logout()

    _currentUserProfile.value = null
    _currentNode.value = null
    _nodesHistory.value = listOf()
    _nodes.value = listOf()
    _companyAllUsers.value = listOf()
    _localUsers.value = listOf()

    afterLogout()
  }
  // endregion

  // region [state && load] Nodes
  private val _currentNode = MutableLiveData<Node?>(null)
  val currentNode: LiveData<Node?>
    get() = _currentNode

  private val _nodes = MutableLiveData<List<Node>>(listOf())


  fun loadNodes() {
    if (_currentNode.value == null)
      tryLoadUserNodes()
    else loadLevelNodes()
  }

  private fun tryLoadUserNodes() {
    if (_currentUserProfile.value == null) loadCurrentUserProfile(onSuccess = { loadUserNodes() })
    else loadUserNodes()
  }

  private fun loadUserNodes() {
    loadNodesFromDBByIDList(nodesIds = _currentUserProfile.value!!.allowedProjects)
  }

  private fun loadLevelNodes() {
    loadNodesFromDBByIDList(nodesIds = _currentNode.value!!.children)
  }

  private fun loadNodesFromDBByIDList(nodesIds: List<String>) {
    changeLoads(LOAD_NODES_FROM_DB_BY_ID_LIST)
//    _state.value = State.LOADING
    viewModelScope.launch {
      val a = NodesDatabaseService
        .getNodesByIDs(nodeIds = nodesIds)
        .awaitAll()
        .mapNotNull {
          it.toObject(Node::class.java)
        }
      setNodes(a)
      changeLoads(LOAD_NODES_FROM_DB_BY_ID_LIST, isAdding = false)
    }
  }

  private fun setNodes(list: List<Node>) {
    changeLoads(SET_NODES)
    _nodes.value = list
//    _state.value = State.VIEW
    filter.value = ""
    filterNodes()
    changeLoads(SET_NODES, isAdding = false)

  }

  private fun updateCurrentNodeDate(node: Node) {
    val id = currentNode.value!!.id
    loadNodeByID(nodeId = id, saveToHistory = false)
    updateHistoryItem(node = node, nodeId = id)
  }

  fun loadNodeByID(nodeId: String?, saveToHistory: Boolean = true) {
    if (nodeId == null) {
      _currentNode.value = null
      _nodesHistory.value = listOf()
      _companyAllUsers.value = listOf()
    } else {
      changeLoads(LOAD_NODE_BY_ID)
      NodesDatabaseService.getNodeById(
        nodeId,
        onFailure = {
          changeLoads(LOAD_NODE_BY_ID, false)
        },
        onSuccess = {
          afterLoadNode(it, saveToHistory)
          changeLoads(LOAD_NODE_BY_ID, false )
        }
      )
    }
  }

  private fun afterLoadNode(node: Node, saveToHistory: Boolean) {
    _currentNode.value = node
    if (saveToHistory) addToHistory(_currentNode.value!!)

    changeLoads(AFTER_LOAD_NODE)

    loadCompanyAllUsers {
      loadLocalUsers {
        findUserRoleInCurrentNode()
      }
    }
    changeLoads(AFTER_LOAD_NODE, false)
//    _state.value = State.VIEW
  }
  // endregion

  // region Node Role
  private val historyRole = MutableLiveData<List<UserRole>>(listOf())

  private val _currentRole = MutableLiveData<UserRole?>(null)
  val currentRole: LiveData<UserRole?>
    get() = _currentRole

  private fun findUserRoleInCurrentNode(navigateTo: Int? = null) {
    if (historyRole.value!!.size == nodesHistory.value!!.size) return

    if (navigateTo == null) {
      val uid = currentUserProfile.value!!.uid
      val userJobFields = _localUsers.value!!.filter { it.jobField.userId == uid }

      var role = _currentRole.value ?: UserRole.READER
      if (userJobFields.isNotEmpty()) {
        userJobFields.forEach {
          val jobFieldRolePriority = it.jobField.jobRole.priority
          if (jobFieldRolePriority < role.priority) {
            role = it.jobField.jobRole
          }
        }
      }

      val roles = historyRole.value!!.toMutableList()
      roles.add(role)
      historyRole.value = roles
    } else {
      historyRole.value = historyRole.value!!.subList(0, navigateTo)
    }

    updateCurrentNodeRole()
    changeLoads(FIND_USER_ROLE_IN_CURRENT_NODE, false)
  }

  private fun updateCurrentNodeRole() {
    _currentRole.value = if (historyRole.value!!.isEmpty()) null else historyRole.value!!.last()
  }
  // endregion

  // region Filter Nodes
  val filter = MutableLiveData("")

  private val _filteredNodes = MutableLiveData<List<Node>>(listOf())
  val filteredNodes: LiveData<List<Node>>
    get() = _filteredNodes

  fun clearFilter() {
    filter.value = ""
    filterNodes()
  }

  fun filterNodes() {
    if (filter.value == "") {
      _filteredNodes.value = _nodes.value
    } else {
      _filteredNodes.value =
        _nodes.value!!.filter { it.name.contains(filter.value!!, ignoreCase = true) }
    }
  }
  // endregion

  // region Edit Nodes
  fun addNodeWithName(name: String) {
    saveWithCheck(name)
  }

  fun deleteNode() {
    changeLoads(DELETE_NODE)
//    _state.value = State.LOADING
    NodesDatabaseService.deleteNode(node = _currentNode.value!!, onFailure = {}) {
      changeLoads(DELETE_NODE, false)
//      _state.value = State.VIEW
      goBack()
    }
  }

  private fun saveWithCheck(name: String) {
    changeLoads(SAVE_NODE_WITH_CHECK)
    val uid = AuthenticationService.getUserUid()!!

    val node = Node(
      name = name,
      level = if (_currentNode.value != null) _currentNode.value!!.level + 1 else 0,
      rootNodeId = if (_currentNode.value != null) _currentNode.value!!.id else null,
      usersHaveAccess = listOf(uid),
      jobs = listOf(JobField.getDefaultHead(uid))
    )

//    _state.value = State.LOADING
    NodesDatabaseService.checkUniqueNodeName(node = node, onFailure = {
      if (it == Errors.nonUniqueName) {
        _state.value = State.NON_UNIQUE_NAME
        changeLoads(SAVE_NODE_WITH_CHECK, false)
      }
    }, onSuccess = {
      _state.value = State.NEW_NODE_ADDED
      addNode(node)
      changeLoads(SAVE_NODE_WITH_CHECK, false)

    })
  }

  private fun addNode(node: Node) {
    changeLoads(ADD_NODE)
//    _state.value = State.LOADING
    NodesDatabaseService.saveNode(
      node,
      onFailure = {
        // TODO: handle error
      },
      onSuccess = {
        val newNodeId = it

        if (node.level == 0) {
          val projects = _currentUserProfile.value!!.allowedProjects.toMutableList()
          projects.add(newNodeId)
          _currentUserProfile.value!!.allowedProjects = projects
        } else {
          val nodes = _currentNode.value!!.children.toMutableList()
          nodes.add(newNodeId)
          _currentNode.value!!.children = nodes
        }

        loadNodes()
        changeLoads(ADD_NODE, false)

      })
  }

  fun changeNodeName(value: String) {
    val node = _currentNode.value!!.copy()
    node.name = value
    changeLoads(CHANGE_NODE_NAME)
    _state.value = State.LOADING
    NodesDatabaseService.checkUniqueNodeName(node = node, onFailure = {
      changeLoads(CHANGE_NODE_NAME, false)
      _state.value = State.NON_UNIQUE_NAME
    }, onSuccess = {
      NodesDatabaseService.changeNodeName(nodeId = node.id, newName = node.name, onFailure = {
        // todo: handle error
      }, onSuccess = {
        updateCurrentNodeDate(node = node)
        changeLoads(CHANGE_NODE_NAME, false)
      })
    })
  }
  // endregion

  // region Node Users
  private val _companyAllUsers = MutableLiveData<List<User>>(listOf())
  val companyAllUsers: LiveData<List<User>>
    get() = _companyAllUsers

  fun getUserById(id: String) = _companyAllUsers.value!!.first { it.uid == id }

  private fun loadCompanyAllUsers(afterLoad: () -> Unit = {}) {
    if (_currentNode.value!!.level != 0) {
      afterLoad()
      return
    }

    changeLoads(LOAD_ALL_COMPANY_USER)
//    _state.value = State.LOADING
    val usersIds = getRootNode()!!.usersHaveAccess
    viewModelScope.launch {
      val users = UsersDatabaseService
        .loadCompanyAllUsers(listUsersID = usersIds)
        .awaitAll()
        .mapNotNull { it.toObject(User::class.java) }
      setUsers(users)
      afterLoad()
      changeLoads(LOAD_ALL_COMPANY_USER, false)
    }
  }

  private fun setUsers(list: List<User>) {
    changeLoads(SET_USERS)
    _companyAllUsers.value = list
//    _state.value = State.VIEW
    filter.value = ""
    filterUsers()
    changeLoads(SET_USERS, false)
  }

  // region Filter Users
  val filterUsersVal = MutableLiveData("")

  private val _filteredUsers = MutableLiveData<List<User>>(listOf())
  val filteredUsers: LiveData<List<User>>
    get() = _filteredUsers

  fun clearFilterUsers() {
    filterUsersVal.value = ""
    filterUsers()
  }

  fun filterUsers() {
    if (filterUsersVal.value == "") {
      _filteredUsers.value = _companyAllUsers.value
    } else {
      val filter = filterUsersVal.value!!
      _filteredUsers.value =
        _companyAllUsers.value!!.filter {
          it.fullName.contains(filter, true) || it.email.contains(filter, true)
        }
    }
  }

  // endregion
  // endregion

  // region LocalUsers
  private val _localUsers = MutableLiveData<List<LocalUser>>(listOf())
  val localUser: LiveData<List<LocalUser>>
    get() = _localUsers

  fun addJobField(jobField: JobField) {
    val jobs = _currentNode.value!!.jobs.toMutableList()
    jobs.add(jobField)
    _currentNode.value!!.jobs = jobs
    loadLocalUsers()
//    _state.value = State.VIEW
  }

  fun updateJobField(oldJobField: JobField, jobField: JobField) {

    val jobs = _currentNode.value!!.jobs.toMutableList()
    jobs.remove(oldJobField)
    jobs.add(jobField)
    _currentNode.value!!.jobs = jobs

    loadLocalUsers()
//    _state.value = State.VIEW
  }

  fun deleteJobField(jobField: JobField) {
    NodesDatabaseService.deleteJobField(
      _currentNode.value!!.id,
      jobField = jobField,
      onFailure = {},
      onSuccess = {
        _localUsers.value = _localUsers.value!!.filter { it.jobField != jobField }
      }
    )
  }

  fun updateLocalUsers() {
    loadLocalUsers {
      findUserRoleInCurrentNode()
    }
  }

  private fun loadLocalUsers(afterLoad: () -> Unit = {}) {
    if (getRootNode() == null) {
      afterLoad()
      return
    }
    changeLoads(LOAD_LOCAL_USERS)
//    _state.value = State.LOADING
    viewModelScope.launch {
      if (_currentNode.value != null) {
        val users = NodesDatabaseService
          .loadCompaniesJobsUsers(_currentNode.value!!.jobs)

        _localUsers.value = users
        checkAliveJobField(
          jobFields = _localUsers.value!!,
          usersIds = getRootNode()!!.usersHaveAccess
        )
        afterLoad()
      }
      changeLoads(LOAD_LOCAL_USERS, false)
//      _state.value = State.VIEW
    }
  }

  private fun checkAliveJobField(jobFields: List<LocalUser>, usersIds: List<String>) {
    val deadJobFields = jobFields.filter { !usersIds.contains(it.jobField.userId) }
    if (deadJobFields.isEmpty()) return

    val nodeId = _currentNode.value!!.id

    deadJobFields.forEach {
      NodesDatabaseService.deleteJobField(
        nodeId,
        jobField = it.jobField,
        onFailure = {},
        onSuccess = {})

    }
    _localUsers.value!!.toMutableList().removeAll(deadJobFields)


    val updatedLocalUsers = deadJobFields.map { localUser ->
      val jobField = localUser.jobField


      val projectRootUserId = getCurrentRootNode().jobs.first { it.jobRole == UserRole.HEAD }.userId
      jobField.userId = projectRootUserId

      localUser.jobField = jobField
      return@map localUser
    }

    updatedLocalUsers.forEach {
      NodesDatabaseService.addJobField(
        nodeId,
        jobField = it.jobField,
        onFailure = {},
        onSuccess = {})
    }
    _localUsers.value!!.toMutableList().addAll(updatedLocalUsers)

    loadLocalUsers()
  }

  private val _editJobField = MutableLiveData<JobField?>()
  val editJobField: LiveData<JobField?> get() = _editJobField

  fun setToEditJobField(jobField: JobField) {
    _editJobField.value = jobField
    _state.value = State.EDIT_JOB_FIELD
  }
  // endregion

  // region User access to project
  private val _addUserError = MutableLiveData<ErrorApp?>(null)
  val addUserError: LiveData<ErrorApp?>
    get() = _addUserError

  /* Предоставляем доступ пользователю к проекту по адрессу эл.почты */
  fun addUserToProjectByEmail(email: String) {
    changeLoads(ADD_USER_USER_BY_EMAIL)
    _state.value = State.LOADING
    NodesDatabaseService.addUserToProjectByEmail(
      email = email,
      currentProjectId = getCurrentRootNode().id,
      onFailure = {
        _addUserError.value = it
        _state.value = State.ADD_USER_ERROR
        changeLoads(ADD_USER_USER_BY_EMAIL, false)
      },
      onSuccess = { user ->
        _state.value = State.ADD_USER_SUCCESS

        if (getRootNode() != null) {
          val uid = user.uid
          updateProjectUsers(isAdding = true, uid)
        }
        changeLoads(ADD_USER_USER_BY_EMAIL, false)
      }
    )
  }

  private fun updateProjectUsers(isAdding: Boolean, uid: String) {
    val users = _nodesHistory.value!![0].usersHaveAccess.toMutableList()

    if (isAdding) users.add(uid) else users.remove(uid)
    _nodesHistory.value!![0].usersHaveAccess = users
    loadCompanyAllUsers {
      loadLocalUsers()
      filterNodes()
    }
  }


  private fun getRootNode() = _nodesHistory.value?.firstOrNull()

  fun denyAccessUser(userId: String) {
    changeLoads(DENY_ACCESS_USER)
//    _state.value = State.LOADING
    val rootId = getRootNode()?.id

    // удалить из root_node usersHaveAccess id пользователя
    if (rootId != null)
      NodesDatabaseService.denyAccessUserToProject(
        rootId, userId,
        onFailure = {
          changeLoads(DENY_ACCESS_USER, false)
        }
      ) {
        // у пользователя удалить из allowedProjects id проекта
        UsersDatabaseService.denyAccessUserToProject(rootId, userId, onFailure = {
          changeLoads(DENY_ACCESS_USER, false)
        }) {
          updateProjectUsers(isAdding = false, userId)
          changeLoads(DENY_ACCESS_USER, false)
        }
      }
  }
  // endregion

  // region Current Measuring
  private val _currentMeasuring = MutableLiveData<Measuring?>()
  val currentMeasuring: LiveData<Measuring?>
    get() = _currentMeasuring

  fun setCurrentMeasuring(measuring: Measuring?) {
    _currentMeasuring.value = measuring
  }

  fun deleteMeasuring(measuring: Measuring) {
    changeLoads(DELETE_MEASURING)
//    _state.value = State.LOADING
    MeasuringDatabaseService.deleteMeasuring(
      measuringId = measuring.measuringID,
      locationNodeId = measuring.passport!!.locationIDNode,
      onFailure = {},
      onSuccess = {
        val measuringItems = _currentNode.value!!.measuring.toMutableList()
        measuringItems.remove(measuring.measuringID)
        _currentNode.value!!.measuring = measuringItems
//        _state.value = State.MEASURING_DELETED
        changeLoads(DELETE_MEASURING, false)

      }
    )
  }

  fun updateMeasuringPart(part: MeasuringPart, value: Any) {
    when (part) {
      MeasuringPart.PASSPORT -> updatePassport(value as MeasuringPassport)
      MeasuringPart.MAINTENANCE_REPAIR -> updateMaintenanceRepair(value as MaintenanceRepair)
      MeasuringPart.OVERHAUL -> updateOverhaul(value as Overhaul)
      MeasuringPart.TO -> updateTO(value as TO)
      MeasuringPart.VERIFICATION -> updateVerification(value as Verification)
    }
  }

  private fun updateVerification(verification: Verification) {
    val measuring = _currentMeasuring.value!!
    measuring.verification = verification
    _currentMeasuring.value = measuring
  }

  private fun updateTO(TO: TO) {
    val measuring = _currentMeasuring.value!!
    measuring.TO = TO
    _currentMeasuring.value = measuring
  }

  private fun updateOverhaul(overhaul: Overhaul) {
    val measuring = _currentMeasuring.value!!
    measuring.overhaul = overhaul
    _currentMeasuring.value = measuring
  }

  private fun updateMaintenanceRepair(maintenanceRepair: MaintenanceRepair) {
    val measuring = _currentMeasuring.value!!
    measuring.maintenanceRepair = maintenanceRepair
    _currentMeasuring.value = measuring
  }

  private fun updatePassport(passport: MeasuringPassport) {
    val measuring = _currentMeasuring.value!!
    measuring.passport = passport
    _currentMeasuring.value = measuring
  }
  // endregion
}