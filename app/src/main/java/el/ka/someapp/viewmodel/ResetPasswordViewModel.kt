package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.State
import el.ka.someapp.data.repository.AuthenticationService
import el.ka.someapp.utils.Verificator

class ResetPasswordViewModel(application: Application) : AndroidViewModel(application) {
  val email = MutableLiveData("")

  private val _state = MutableLiveData(State.ENTER_DATA)
  val state: LiveData<State>
    get() = _state

  private val _errors = MutableLiveData<Array<ErrorApp>>()
  val errors: LiveData<Array<ErrorApp>>
    get() = _errors

  fun onContinue() {

    if (_state.value == State.AWAITING_CONTINUE || _state.value == State.AWAITING) {
      _state.value = State.AWAITING
    } else {
      _state.value = State.LOADING
      _errors.value = Verificator.checkEmail(email = email.value.toString())
      if (_errors.value!!.isEmpty()) tryResetPassword() else _state.value = State.ENTER_DATA
    }
  }

  private fun tryResetPassword() {
    _state.value = State.LOADING
    AuthenticationService.resetPassword(
      email = email.value.toString(),
      onSuccess = {
        _state.value = State.AWAITING_CONTINUE
      },
      onFailure = { error ->
        _errors.value = arrayOf(error)
        _state.value = State.ENTER_DATA
      }
    )
  }

}