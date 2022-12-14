package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.State
import el.ka.someapp.data.repository.AuthenticationService

class DefenderViewModel(application: Application) : AndroidViewModel(application) {
  private val auth = AuthenticationService
  private val _statePassword = MutableLiveData(StatePassword.NO_NEW)
  val statePassword: LiveData<StatePassword>
    get() = _statePassword

  private val _field = MutableLiveData("")
  val field: LiveData<String>
    get() = _field

  private val _fieldRepeat = MutableLiveData("")

  fun deleteLetter() {
    val str = _field.value!!
    if (str == "") return
    _field.value = str.substring(0, str.length - 1)
  }

  private val _correctPassword = MutableLiveData("")
  fun setCorrectPassword(value: String) {
    if (value == "") {
      _statePassword.value = StatePassword.NEW
    } else {
      _correctPassword.value = value
      _statePassword.value = StatePassword.NO_NEW
    }
  }

  fun addPasswordElement(element: Char) {
    if (field.value!!.length < 5) {
      _field.value = field.value + element
    }

    if (field.value!!.length >= 5) {
      if (statePassword.value == StatePassword.NEW
        || statePassword.value == StatePassword.NEW_WITH_ERROR
        || statePassword.value == StatePassword.NO_NEW
        || statePassword.value == StatePassword.REPEAT_NEW
      ) {
        onPasswordFull()
      }
    }
  }

  private fun onPasswordFull() {
    when (_statePassword.value) {
      StatePassword.NEW, StatePassword.NEW_WITH_ERROR -> goRepeatPassword()
      StatePassword.REPEAT_NEW -> checkPasswordRepeat()
      StatePassword.NO_NEW -> checkPassword()
      else -> {}
    }
  }

  private fun checkPassword() {
    if (field.value == _correctPassword.value) {
      _statePassword.value = StatePassword.AWAITING
    } else {
      // TODO: show error
      while (_field.value!!.isNotEmpty()) deleteLetter()
      _state.value = State.ERROR
    }
  }

  private val _state = MutableLiveData(State.VIEW)
  val state: LiveData<State>
    get() = _state

  fun resumeState() {
    _state.value = State.VIEW
  }

  private fun checkPasswordRepeat() {
    if (_fieldRepeat.value == _field.value) {
      _statePassword.value = StatePassword.AWAITING_WITH_SAVE
    } else {
      _field.value = ""
      _fieldRepeat.value = ""
      _statePassword.value = StatePassword.NEW_WITH_ERROR
    }
  }

  private fun goRepeatPassword() {
    _fieldRepeat.value = field.value
    _field.value = ""
    _statePassword.value = StatePassword.REPEAT_NEW
  }

  fun logout() {
    auth.logout()
    _statePassword.value = StatePassword.LOGOUT
  }
}

enum class StatePassword {
  NEW, REPEAT_NEW, NO_NEW, AWAITING, AWAITING_WITH_SAVE, LOGOUT, NEW_WITH_ERROR
}