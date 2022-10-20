package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.Node
import el.ka.someapp.data.model.State
import el.ka.someapp.data.repository.AuthenticationService
import el.ka.someapp.data.repository.CloudDatabaseService

class NodesViewModel(application: Application) : AndroidViewModel(application) {
  private val _nodes = MutableLiveData<List<Node>>(listOf())
  val nodes: LiveData<List<Node>>
    get() = _nodes

  private val _state = MutableLiveData<State>(State.VIEW)
  val state: LiveData<State>
    get() = _state

  fun loadMainNodes() {
    _state.value = State.LOADING
    CloudDatabaseService.getUserMainNodes(
      userId = AuthenticationService.getUserUid()!!,
      onSuccess = {
        _nodes.value = it
        _state.value = State.VIEW
      },
      onFailure = {
        // TODO: Handle Errors
        _state.value = State.VIEW
      }
    )
  }
}