package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.Node
import el.ka.someapp.data.model.State
import el.ka.someapp.data.repository.AuthenticationService
import el.ka.someapp.data.repository.CloudDatabaseService

class NodesViewModel(application: Application) : AndroidViewModel(application) {
  //private val _currentLevel = MutableLiveData<Int>(-1)  // -1 = main menu with companies
  //private val _currentRoot = MutableLiveData<String?>(null)

  val currentNode = MutableLiveData<Node?>(null)

  private val _nodes = MutableLiveData<List<Node>>(listOf())

  private val _state = MutableLiveData(State.VIEW)
  val state: LiveData<State>
    get() = _state

  val filter = MutableLiveData("")

  private val _filteredNodes = MutableLiveData<List<Node>>(listOf())
  val filteredNodes: LiveData<List<Node>>
    get() = _filteredNodes

  fun filterNodes() {
    if (filter.value == "") {
      _filteredNodes.value = _nodes.value
    } else {
      _filteredNodes.value =
        _nodes.value!!.filter { it.name.contains(filter.value!!, ignoreCase = true) }
    }
  }

  fun loadNodes() {
    if (currentNode.value == null) loadMainNodes() else loadLevelNodes()
  }

  private fun loadLevelNodes() {
    _state.value = State.LOADING
    CloudDatabaseService.getNotesInLevelRoot(
      root = currentNode.value!!.rootNodeId,
      level = currentNode.value!!.level + 1,
      onFailure = { onNodesLoadFailure() },
      onSuccess = { setNodes(it) }
    )
  }

  private fun loadMainNodes() {
    _state.value = State.LOADING
    CloudDatabaseService.getUserMainNodes(
      userId = AuthenticationService.getUserUid()!!,
      onSuccess = { setNodes(it) },
      onFailure = { onNodesLoadFailure() }
    )
  }

  private fun onNodesLoadFailure() {
    // TODO: handle error
    _state.value = State.VIEW
  }

  private fun setNodes(list: List<Node>) {
    _nodes.value = list
    _state.value = State.VIEW
    filter.value = ""
    filterNodes()
  }

  fun addNodeWithName(name: String) {
    saveWithCheck(name)
  }

  private fun saveWithCheck(name: String) {
    val node = Node(
      name = name,
      level = if (currentNode.value != null) currentNode.value!!.level + 1 else 0,
      rootNodeId = if (currentNode.value != null) currentNode.value!!.id else null,
      head = listOf(AuthenticationService.getUserUid()!!),
    )

    _state.value = State.LOADING
    CloudDatabaseService.checkUniqueNodeName(
      node = node,
      onFailure = {
        if (it == Errors.nonUniqueName) {
          _state.value = State.NON_UNIQUE_NAME
        }
      },
      onSuccess = {
        _state.value = State.NEW_NODE_ADDED
        addNode(node)
      }
    )
  }

  private fun addNode(node: Node) {
    _state.value = State.LOADING
    CloudDatabaseService.saveNode(
      node,
      onFailure = {},
      onSuccess = { loadNodes() })
  }

  fun toViewState() {
    _state.value = State.VIEW
  }

  fun loadNodeByID(nodeId: String?) {
    if (nodeId == null) {
      currentNode.value = null
    } else {
      _state.value = State.LOADING
      CloudDatabaseService.getNodesById(
        nodeId,
        onFailure = {
          // TODO: handle error
        },
        onSuccess = {
          currentNode.value = it
          _state.value = State.VIEW
        })
    }
  }
}