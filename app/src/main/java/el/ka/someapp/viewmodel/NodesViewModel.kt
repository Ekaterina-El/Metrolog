package el.ka.someapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import el.ka.someapp.data.model.*
import el.ka.someapp.data.model.measuring.*
import el.ka.someapp.data.repository.AuthenticationService
import el.ka.someapp.data.repository.MeasuringDatabaseService
import el.ka.someapp.data.repository.NodesDatabaseService
import el.ka.someapp.data.repository.UsersDatabaseService
import el.ka.someapp.general.Loads
import el.ka.someapp.general.byCategory
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


  private val _loadMeasuringState =
    MutableLiveData(LoadMeasuringState.CURRENT)
  val loadMeasuringState: LiveData<LoadMeasuringState> get() = _loadMeasuringState

  fun setLoadMeasuringState(loadMeasuringState: LoadMeasuringState) {
    _loadMeasuringState.value = loadMeasuringState
    loadMeasuringByState()
  }

  fun loadMeasuringByState() {
    val node = _currentNode.value ?: return

    when (_loadMeasuringState.value) {
      LoadMeasuringState.ALL -> loadAllNodesMeasuring(node)
      LoadMeasuringState.CURRENT -> loadMeasuring(node.measuring)
      else -> {}
    }
  }

  fun loadCurrentMeasuringHistory() {
    changeLoads(Loads.LOAD_MEASURING_HISTORY)
    viewModelScope.launch {
      val historyItemsExecuted = _currentMeasuring.value!!.history
        .map {
          val uid = it.userId
          return@map MeasuringHistoryItemExecuted(
            history = it,
            user = getUserFromAll(uid) ?: loadUserByUid(uid)
          )
        }
      _currentMeasuringHistory.value = historyItemsExecuted.byCategory()
      changeLoads(Loads.LOAD_MEASURING_HISTORY, false)
    }
  }

  private val _currentMeasuringHistory = MutableLiveData<List<CategoryHistory>>()
  val currentMeasuringHistory: LiveData<List<CategoryHistory>>
    get() = _currentMeasuringHistory

  private suspend fun loadUserByUid(uid: String): User =
    UsersDatabaseService.getUserByUid(uid).toObject(User::class.java)!!

  private fun getUserFromAll(uid: String) = _companyAllUsers.value!!.firstOrNull { it.uid == uid }

  private fun loadMeasuring(measuringIds: List<String>) {
    changeLoads(Loads.LOAD_MEASURING)
    viewModelScope.launch {
      val measuringItems = MeasuringDatabaseService
        .getMeasuringByIDs(measuringIds)
        .awaitAll()
        .mapNotNull {
          val measuring = it.toObject(Measuring::class.java)
          if (measuring != null) measuring.measuringID = it.id
          measuring
        }
      setMeasuring(measuringItems)
      changeLoads(Loads.LOAD_MEASURING, false)
    }
  }

  private fun setMeasuring(measuringItems: List<Measuring>) {
    changeLoads(Loads.SET_MEASURING)
    _measuring.value = measuringItems
    filterMeasuring()
    changeLoads(Loads.SET_MEASURING, false)
  }

  fun filterMeasuring() {
    val f = filterFieldMeasuring.value!!
    _measuringFiltered.value =
      if (f.isEmpty()) _measuring.value
      else _measuring.value!!.filter {
        return@filter it.passport.name.contains(f, true) ||
            it.passport.type.contains(f, true) ||
            it.passport.inventoryNumber!!.contains(f, true)
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

  private fun loadAllNodesMeasuring(rootNode: Node) {
    changeLoads(Loads.LOAD_ALL_NODES_MEASURING)
    val measuringIds = mutableListOf<String>()
    measuringIds.addAll(rootNode.measuring)     // Добавляем id СИ рутового елемента

    viewModelScope.launch {
      // Загрузить все узлы и узлы узлов  |  Взять id СИ
      rootNode.children.forEach {
        // Загружаем узел
        loadNodes(it).forEach { node ->
          measuringIds.addAll(node.measuring)    // Берём id СИ, хранящихся в узле
        }
      }
      // Загрузить общий список measuring

      loadMeasuring(measuringIds)
      changeLoads(Loads.LOAD_ALL_NODES_MEASURING, false)
    }
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
    changeLoads(Loads.NAVIGATE_BY_HISTORY_TO)
//    _state.value = State.LOADING

    // получить индекс nodeID
    val idx = _nodesHistory.value!!.indexOf(node)

    // преобрзование списка истории узлов
    _nodesHistory.value = _nodesHistory.value!!.subList(0, idx + 1)
    findUserRoleInCurrentNode(idx + 1)

    // загрузить данные о текущем node
    loadNodeByID(node.id, false)
    changeLoads(Loads.NAVIGATE_BY_HISTORY_TO, false)
  }
  // endregion

  // region User
  private val _currentUserProfile = MutableLiveData<User?>(null)
  val currentUserProfile: LiveData<User?>
    get() = _currentUserProfile

  fun loadCurrentUserProfile(onSuccess: () -> Unit = {}) {
    changeLoads(Loads.LOAD_CURRENT_USER_PROFILE)
    UsersDatabaseService.loadCurrentUserProfile(
      onFailure = {
        changeLoads(Loads.LOAD_CURRENT_USER_PROFILE, isAdding = false)
      },
      onSuccess = {
        _currentUserProfile.value = it
        onSuccess()
        changeLoads(Loads.LOAD_CURRENT_USER_PROFILE, isAdding = false)
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

  private suspend fun loadNodes(rootNodeId: String): List<Node> {
    val list = mutableListOf<Node>()

    val node = NodesDatabaseService.getNodeByID(rootNodeId).toObject(Node::class.java)!!
    list.add(node)
    val chd = node.children
    if (chd.isNotEmpty()) chd.forEach { list.addAll(loadNodes(it)) }

    return list
  }

  fun loadNodes() {
    if (_currentNode.value == null) tryLoadUserNodes()
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
    changeLoads(Loads.LOAD_NODES_FROM_DB_BY_ID_LIST)
    viewModelScope.launch {
      try {
        val a = NodesDatabaseService
          .getNodesByIDs(nodeIds = nodesIds)
          .awaitAll()
          .mapNotNull {
            it.toObject(Node::class.java)
          }
        setNodes(a)
        changeLoads(Loads.LOAD_NODES_FROM_DB_BY_ID_LIST, isAdding = false)
      } catch (e: FirebaseFirestoreException) {
        changeLoads(Loads.LOAD_NODES_FROM_DB_BY_ID_LIST, isAdding = false)
        catchNetworkError()
      }
    }
  }

  private fun catchNetworkError() {
    _state.value = State.NETWORK_ERROR
  }

  private fun setNodes(list: List<Node>) {
    changeLoads(Loads.SET_NODES)
    _nodes.value = list
    filter.value = ""
    filterNodes()
    changeLoads(Loads.SET_NODES, isAdding = false)

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
      changeLoads(Loads.LOAD_NODE_BY_ID)
      NodesDatabaseService.getNodeById(
        nodeId,
        onFailure = {
          changeLoads(Loads.LOAD_NODE_BY_ID, false)
        },
        onSuccess = {
          afterLoadNode(it, saveToHistory)
          changeLoads(Loads.LOAD_NODE_BY_ID, false)
        }
      )
    }
  }

  private fun afterLoadNode(node: Node, saveToHistory: Boolean) {
    _currentNode.value = node
    if (saveToHistory) addToHistory(_currentNode.value!!)

    changeLoads(Loads.AFTER_LOAD_NODE)

    loadCompanyAllUsers {
      loadLocalUsers {
        findUserRoleInCurrentNode()
      }
    }
    changeLoads(Loads.AFTER_LOAD_NODE, false)
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

    changeLoads(Loads.FIND_USER_ROLE_IN_CURRENT_NODE)
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
    changeLoads(Loads.FIND_USER_ROLE_IN_CURRENT_NODE, false)
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
    changeLoads(Loads.DELETE_NODE)
//    _state.value = State.LOADING
    NodesDatabaseService.deleteNode(node = _currentNode.value!!, onFailure = {}) {
      changeLoads(Loads.DELETE_NODE, false)
//      _state.value = State.VIEW
      goBack()
    }
  }

  private fun saveWithCheck(name: String) {
    changeLoads(Loads.SAVE_NODE_WITH_CHECK)
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
        changeLoads(Loads.SAVE_NODE_WITH_CHECK, false)
      }
    }, onSuccess = {
      _state.value = State.NEW_NODE_ADDED
      addNode(node)
      changeLoads(Loads.SAVE_NODE_WITH_CHECK, false)

    })
  }

  private fun addNode(node: Node) {
    changeLoads(Loads.ADD_NODE)
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
        changeLoads(Loads.ADD_NODE, false)

      })
  }

  fun changeNodeName(value: String) {
    val node = _currentNode.value!!.copy()
    node.name = value
    changeLoads(Loads.CHANGE_NODE_NAME)
    _state.value = State.LOADING
    NodesDatabaseService.checkUniqueNodeName(node = node, onFailure = {
      changeLoads(Loads.CHANGE_NODE_NAME, false)
      _state.value = State.NON_UNIQUE_NAME
    }, onSuccess = {
      NodesDatabaseService.changeNodeName(nodeId = node.id, newName = node.name, onFailure = {
        // todo: handle error
      }, onSuccess = {
        updateCurrentNodeDate(node = node)
        changeLoads(Loads.CHANGE_NODE_NAME, false)
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

    changeLoads(Loads.LOAD_ALL_COMPANY_USER)
//    _state.value = State.LOADING
    val usersIds = getRootNode()!!.usersHaveAccess
    viewModelScope.launch {
      val users = UsersDatabaseService
        .loadCompanyAllUsers(listUsersID = usersIds)
        .awaitAll()
        .mapNotNull { it.toObject(User::class.java) }
      setUsers(users)
      afterLoad()
      changeLoads(Loads.LOAD_ALL_COMPANY_USER, false)
    }
  }

  private fun setUsers(list: List<User>) {
    changeLoads(Loads.SET_USERS)
    _companyAllUsers.value = list
//    _state.value = State.VIEW
    filter.value = ""
    filterUsers()
    changeLoads(Loads.SET_USERS, false)
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
        _currentNode.value!!.jobs = _currentNode.value!!.jobs.filter { it != jobField }
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
    changeLoads(Loads.LOAD_LOCAL_USERS)
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
      changeLoads(Loads.LOAD_LOCAL_USERS, false)
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
    changeLoads(Loads.ADD_USER_USER_BY_EMAIL)
    _state.value = State.LOADING
    NodesDatabaseService.addUserToProjectByEmail(
      email = email,
      currentProjectId = getCurrentRootNode().id,
      onFailure = {
        _addUserError.value = it
        _state.value = State.ADD_USER_ERROR
        changeLoads(Loads.ADD_USER_USER_BY_EMAIL, false)
      },
      onSuccess = { user ->
        _state.value = State.ADD_USER_SUCCESS

        if (getRootNode() != null) {
          val uid = user.uid
          updateProjectUsers(isAdding = true, uid)
        }
        changeLoads(Loads.ADD_USER_USER_BY_EMAIL, false)
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
    changeLoads(Loads.DENY_ACCESS_USER)
//    _state.value = State.LOADING
    val rootId = getRootNode()?.id

    // удалить из root_node usersHaveAccess id пользователя
    if (rootId != null)
      NodesDatabaseService.denyAccessUserToProject(
        rootId, userId,
        onFailure = {
          changeLoads(Loads.DENY_ACCESS_USER, false)
        }
      ) {
        // у пользователя удалить из allowedProjects id проекта
        UsersDatabaseService.denyAccessUserToProject(rootId, userId, onFailure = {
          changeLoads(Loads.DENY_ACCESS_USER, false)
        }) {
          updateProjectUsers(isAdding = false, userId)
          changeLoads(Loads.DENY_ACCESS_USER, false)
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
    changeLoads(Loads.DELETE_MEASURING)
    MeasuringDatabaseService.deleteMeasuring(
      measuringId = measuring.measuringID,
      locationNodeId = measuring.passport.locationIDNode,
      onFailure = {},
      onSuccess = {
        val measuringItems = _currentNode.value!!.measuring.toMutableList()
        measuringItems.remove(measuring.measuringID)
        _currentNode.value!!.measuring = measuringItems
        changeLoads(Loads.DELETE_MEASURING, false)

      }
    )
  }

  fun updateMeasuringPart(part: MeasuringPart, value: Any) {
    val measuring = _currentMeasuring.value!!

    when (part) {
      MeasuringPart.PASSPORT -> measuring.passport = value as MeasuringPassport
      MeasuringPart.MAINTENANCE_REPAIR -> measuring.maintenanceRepair = value as MaintenanceRepair
      MeasuringPart.OVERHAUL -> measuring.overhaul = value as Overhaul
      MeasuringPart.TO -> measuring.TO = value as TO
      MeasuringPart.VERIFICATION -> measuring.verification = value as Verification
      MeasuringPart.CERTIFICATION -> measuring.certification = value as Certification
      MeasuringPart.CALIBRATION -> measuring.calibration = value as Calibration
      MeasuringPart.HISTORY -> measuring.history =
        (value as List<*>).map { it as MeasuringHistoryItem }
    }

    _currentMeasuring.value = measuring
  }

  fun getLocalUserHeadID(): String? {
    return _localUsers.value!!.firstOrNull { it.jobField.jobRole == UserRole.HEAD }?.user?.uid
  }
}
// endregion