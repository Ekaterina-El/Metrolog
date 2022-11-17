package el.ka.someapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.someapp.data.model.*
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.data.repository.AuthenticationService
import el.ka.someapp.data.repository.MeasuringDatabaseService
import el.ka.someapp.data.repository.NodesDatabaseService
import el.ka.someapp.data.repository.UsersDatabaseService
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class NodesViewModel(application: Application) : AndroidViewModel(application) {
  private fun clearFields() {
    filterFieldMeasuring.value = ""
    _measuring.value = listOf()
    _filteredNodes.value = listOf()
    _nodesHistory.value = listOf()
    _currentNode.value = null
    _nodes.value = listOf()
    filter.value = ""
    _filteredNodes.value = listOf()
    _companyAllUsers.value = listOf()
    filterUsersVal.value = ""
    _filteredUsers.value = listOf()
    _localUsers.value = listOf()
  }

  private val _state = MutableLiveData(State.VIEW)
  val state: LiveData<State>
    get() = _state

  fun toViewState() {
    _state.value = State.VIEW
  }

  // region Measuring
  val filterFieldMeasuring = MutableLiveData("")

  private val _measuring = MutableLiveData<List<Measuring>>(listOf())

  private val _measuringFiltered = MutableLiveData<List<Measuring>>(listOf())
  val measuringFiltered: LiveData<List<Measuring>> get() = _measuringFiltered

  fun loadMeasuring() {
    _state.value = State.LOADING
    viewModelScope.launch {
      val measuringItems = MeasuringDatabaseService
        .getMeasuringByIDs(_currentNode.value!!.measuring)
        .awaitAll()
        .mapNotNull {
          val measuring = it.toObject(Measuring::class.java)
          measuring!!.measuringID = it.id
          measuring
        }

      setMeasuring(measuringItems)
    }
  }

  private fun setMeasuring(measuringItems: List<Measuring>) {
    _measuring.value = measuringItems
    filterMeasuring()
    _state.value = State.VIEW
  }

  fun filterMeasuring() {
    val f = filterFieldMeasuring.value!!
    _measuringFiltered.value =
      if (f.isEmpty()) _measuring.value
      else _measuring.value!!.filter { it.passport!!.name.contains(f, true) }
  }

  fun addMeasuringToLocal(measuringId: String) {
    val measuringItems = _currentNode.value!!.measuring.toMutableList()
    measuringItems.add(measuringId)
    _currentNode.value!!.measuring = measuringItems
  }
  // endregion

  // region History
  private val _nodesHistory = MutableLiveData<List<Node>>(listOf())
  val nodesHistory: LiveData<List<Node>>
    get() = _nodesHistory

  private fun getCurrentRootNode() = _nodesHistory.value!![0].id

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
    _state.value = State.LOADING

    // получить индекс nodeID
    val idx = _nodesHistory.value!!.indexOf(node)

    // преобрзование списка истории узлов
    _nodesHistory.value = _nodesHistory.value!!.subList(0, idx + 1)

    // загрузить данные о текущем node
    loadNodeByID(node.id, false)
  }
  // endregion

  // region User
  private val _currentUserProfile = MutableLiveData<User?>(null)
  val currentUserProfile: LiveData<User?>
    get() = _currentUserProfile

  fun loadCurrentUserProfile(onSuccess: () -> Unit = {}) {
    _state.value = State.LOADING
    UsersDatabaseService.loadCurrentUserProfile(
      onFailure = {},
      onSuccess = {
        _currentUserProfile.value = it
        _state.value = State.VIEW
        onSuccess()
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
    _state.value = State.LOADING
    viewModelScope.launch {
      val a = NodesDatabaseService
        .getNodesByIDs(nodeIds = nodesIds)
        .awaitAll()
        .mapNotNull {
          it.toObject(Node::class.java)
        }
      setNodes(a)
    }
  }

  private fun setNodes(list: List<Node>) {
    _nodes.value = list
    _state.value = State.VIEW
    filter.value = ""
    filterNodes()
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
      _state.value = State.LOADING
      NodesDatabaseService.getNodeById(
        nodeId,
        onFailure = {
          // TODO: handle error
          _state.value = State.ERROR
        },
        onSuccess = { afterLoadNode(it, saveToHistory) })
    }
  }

  private fun afterLoadNode(node: Node, saveToHistory: Boolean) {
    _currentNode.value = node
    if (saveToHistory) addToHistory(_currentNode.value!!)

    loadLocalUsers()
    loadCompanyAllUsers()
    _state.value = State.VIEW
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

  private fun saveWithCheck(name: String) {
    val uid = AuthenticationService.getUserUid()!!

    val node = Node(
      name = name,
      level = if (_currentNode.value != null) _currentNode.value!!.level + 1 else 0,
      rootNodeId = if (_currentNode.value != null) _currentNode.value!!.id else null,
      usersHaveAccess = listOf(uid),
      jobs = listOf(JobField.getDefaultHead(uid))
    )

    _state.value = State.LOADING
    NodesDatabaseService.checkUniqueNodeName(node = node, onFailure = {
      if (it == Errors.nonUniqueName) {
        _state.value = State.NON_UNIQUE_NAME
      }
    }, onSuccess = {
      _state.value = State.NEW_NODE_ADDED
      addNode(node)
    })
  }

  private fun addNode(node: Node) {
    _state.value = State.LOADING
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
      })
  }

  fun changeNodeName(value: String) {
    val node = _currentNode.value!!.copy()
    node.name = value
    _state.value = State.LOADING
    NodesDatabaseService.checkUniqueNodeName(node = node, onFailure = {
      _state.value = State.NON_UNIQUE_NAME
    }, onSuccess = {
      NodesDatabaseService.changeNodeName(nodeId = node.id, newName = node.name, onFailure = {
        // todo: handle error
      }, onSuccess = {
        updateCurrentNodeDate(node = node)
      })
    })
  }
  // endregion

  // region Node Users
  private val _companyAllUsers = MutableLiveData<List<User>>(listOf())
  val companyAllUsers: LiveData<List<User>>
    get() = _companyAllUsers

  fun getUserById(id: String) = _companyAllUsers.value!!.first { it.uid == id }

  private fun loadCompanyAllUsers() {
    if (_currentNode.value!!.level != 0) return

    _state.value = State.LOADING
//    val usersIds = _currentNode.value!!.usersHaveAccess
    val usersIds = getRootNode()!!.usersHaveAccess
    viewModelScope.launch {
      val users = UsersDatabaseService
        .loadCompanyAllUsers(listUsersID = usersIds)
        .awaitAll()
        .mapNotNull { it.toObject(User::class.java) }
      setUsers(users)
    }
  }

  private fun setUsers(list: List<User>) {
    _companyAllUsers.value = list
    _state.value = State.VIEW
    filter.value = ""
    filterUsers()
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
      _filteredUsers.value =
        _companyAllUsers.value!!.filter {
          it.fullName.contains(
            filterUsersVal.value!!,
            ignoreCase = true
          )
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
    _state.value = State.VIEW
  }

  fun updateJobField(oldJobField: JobField, jobField: JobField) {
    _currentNode.value!!.jobs = _currentNode.value!!.jobs.map {
      return@map if (it == oldJobField) jobField else it
    }

    loadLocalUsers()
    _state.value = State.VIEW
  }

  fun deleteJobField(jobField: JobField) {
    NodesDatabaseService.deleterJobField(
      _currentNode.value!!.id,
      jobField = jobField,
      onFailure = {},
      onSuccess = {
        _localUsers.value = _localUsers.value!!.filter {
          it.jobField != jobField
        }
//         удалить из локального состояния currentNode
      }
    )

  }

  private fun loadLocalUsers() {
    _state.value = State.LOADING
    viewModelScope.launch {
      val users = NodesDatabaseService
        .loadCompaniesJobsUsers(_currentNode.value!!.jobs)
      _localUsers.value = users
      _state.value = State.VIEW
    }
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
    _state.value = State.LOADING
    NodesDatabaseService.addUserToProjectByEmail(
      email = email,
      currentProjectId = getCurrentRootNode(),
      onFailure = {
        _addUserError.value = it
        _state.value = State.ADD_USER_ERROR
      },
      onSuccess = { user ->
        _state.value = State.ADD_USER_SUCCESS

        val users = _companyAllUsers.value!!.toMutableList()
        users.add(user)
        _companyAllUsers.value = users
        filterUsers()

      }
    )
  }

  private fun getRootNode() = _nodesHistory.value?.first()

  fun denyAccessUser(userId: String) {
    _state.value = State.LOADING
    val rootId = getRootNode()?.id

    // удалить из root_node usersHaveAccess id пользователя
    if (rootId != null)
      NodesDatabaseService.denyAccessUserToProject(
        rootId, userId,
        onFailure = {
          State.VIEW
        }
      ) {
        // у пользователя удалить из allowedProjects id проекта
        UsersDatabaseService.denyAccessUserToProject(rootId, userId, onFailure = {
          State.VIEW
        }) {
          _nodesHistory.value?.mapIndexed { index, node ->
            if (index == 0) {
              val users = node.usersHaveAccess.toMutableList()
              users.remove(userId)
              node.usersHaveAccess = users
            }
            return@mapIndexed node
          }

//          _companyAllUsers.value = _companyAllUsers.value
          loadCompanyAllUsers()

          _state.value = State.VIEW
        }
      }
  }
  // endregion
}