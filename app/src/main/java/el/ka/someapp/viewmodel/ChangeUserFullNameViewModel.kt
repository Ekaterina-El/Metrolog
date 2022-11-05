package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.*
import el.ka.someapp.data.repository.NodesDatabaseService
import el.ka.someapp.data.repository.UsersDatabaseService
import el.ka.someapp.utils.Verificator

class ChangeUserFullNameViewModel(application: Application) : AndroidViewModel(application) {
  var newFullName = MutableLiveData("")

  private val _error = MutableLiveData<ErrorApp?>(null)
  val error: LiveData<ErrorApp?>
    get() = _error

  private val _state = MutableLiveData<State>(State.VIEW)
  val state: LiveData<State>
    get() = _state

  fun truChangeFullName() {
    _state.value = State.LOADING
    val errors = Verificator.checkName(newFullName.value!!)
    if (errors.isNotEmpty()) {
      _error.value = errors[0]
      _state.value = State.VIEW
    } else changeFullName()
  }

  private fun changeFullName() {
    UsersDatabaseService.changeProfileFullName(
      newFullName = newFullName.value!!,
      onFailure = {},
      onSuccess = {
        _state.value = State.FULL_NAME_CHANGED
        clearJobField()
      }
    )
  }

  private fun clearJobField() {
    newFullName.value = ""
  }
}