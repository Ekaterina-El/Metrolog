package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.Node
import el.ka.someapp.data.model.State
import el.ka.someapp.data.repository.AuthenticationService
import el.ka.someapp.data.repository.CloudDatabaseService
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class NodesViewModel(application: Application) : AndroidViewModel(application) {
  // region History
  private val _nodesHistory = MutableLiveData<List<Node>>(listOf())
  val nodesHistory: LiveData<List<Node>>
    get() = _nodesHistory

  private fun addToHistory(node: Node) {
    if (_nodesHistory.value!!.isEmpty() || _nodesHistory.value!!.last().id != node.id) {
      val history = _nodesHistory.value!!.toMutableList()
      history.add(node)
      _nodesHistory.value = history.toList()
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
      _state.value = State.BACK
    } else {
      loadNodeByID(_nodesHistory.value!!.last().id)
    }
  }

  fun navigateByHistoryTo(node: Node) {
    if (node.id == _currentNode.value!!.id)  return
    _state.value = State.LOADING

    // получить индекс nodeID
    val idx = _nodesHistory.value!!.indexOf(node)

    // преобрзование списка истории узлов
    _nodesHistory.value = _nodesHistory.value!!.subList(0, idx+1)

    // загрузить данные о текущем node
    loadNodeByID(node.id, false)
  }
  // endregion

  private val _currentNode = MutableLiveData<Node?>(null)
  val currentNode: LiveData<Node?>
    get() = _currentNode

  private val _nodes = MutableLiveData<List<Node>>(listOf())

  private val _state = MutableLiveData(State.VIEW)
  val state: LiveData<State>
    get() = _state

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

  fun loadNodes() {
    if (_currentNode.value == null) loadMainNodes() else loadLevelNodes()
  }

  private fun loadLevelNodes() {
    _state.value = State.LOADING
    viewModelScope.launch {
      val a = CloudDatabaseService.getNodesInLevelRoot(
        children = _currentNode.value!!.children
      ).awaitAll().map { it.toObject(Node::class.java)!! }
      setNodes(a)
    }
  }

  private fun loadMainNodes() {
    _state.value = State.LOADING
    CloudDatabaseService.getUserMainNodes(userId = AuthenticationService.getUserUid()!!,
      onSuccess = { setNodes(it) },
      onFailure = { onNodesLoadFailure() })
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
      level = if (_currentNode.value != null) _currentNode.value!!.level + 1 else 0,
      rootNodeId = if (_currentNode.value != null) _currentNode.value!!.id else null,
      head = listOf(AuthenticationService.getUserUid()!!),
    )

    _state.value = State.LOADING
    CloudDatabaseService.checkUniqueNodeName(node = node, onFailure = {
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
    CloudDatabaseService.saveNode(node, onFailure = {
      // TODO: handle error
    }, onSuccess = {
      loadNodes()
    })
  }

  fun toViewState() {
    _state.value = State.VIEW
  }

  fun loadNodeByID(nodeId: String?, saveToHistory: Boolean = true) {
    if (nodeId == null) {
      _currentNode.value = null
      _nodesHistory.value = listOf()
    } else {
      _state.value = State.LOADING
      CloudDatabaseService.getNodeById(nodeId, onFailure = {
        // TODO: handle error
      }, onSuccess = {
        _currentNode.value = it
        if (saveToHistory) addToHistory(_currentNode.value!!)
        _state.value = State.VIEW
      })
    }
  }

  fun changeNodeName(value: String) {
    val node = _currentNode.value!!.copy()
    node.name = value
    _state.value = State.LOADING
    CloudDatabaseService.checkUniqueNodeName(node = node, onFailure = {
      _state.value = State.NON_UNIQUE_NAME
    }, onSuccess = {
      CloudDatabaseService.changeNodeName(nodeId = node.id, newName = node.name, onFailure = {
        // todo: handle error
      }, onSuccess = {
        updateCurrentNodeDate()
      })
    })
  }

  private fun updateCurrentNodeDate() {
    val id = currentNode.value!!.id
    loadNodeByID(nodeId = id, saveToHistory = false)
  }
}