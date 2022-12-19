package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.State
import el.ka.someapp.data.repository.AuthenticationService
import el.ka.someapp.utils.Verificator

class LoginViewModel(application: Application) : AndroidViewModel(application) {
  private val auth = AuthenticationService

  private val _showPassword = MutableLiveData(false)
  val showPassword: LiveData<Boolean>
    get() = _showPassword

  var email = MutableLiveData<String>()
  var password = MutableLiveData<String>()

  private val _errors = MutableLiveData(mutableListOf<ErrorApp>())
  val errors: MutableLiveData<MutableList<ErrorApp>>
    get() = _errors


  private val _state = MutableLiveData(State.ENTER_DATA)
  val state: LiveData<State>
    get() = _state

  init {
    email.value = ""
    password.value = ""
  }

  fun changeShowPasswordState() {
    _showPassword.value = !_showPassword.value!!
  }

  fun verificationCredentials() {
    _errors.value = Verificator.checkForSignIn(
      email = email.value!!,
      password = password.value!!
    )

    if (_errors.value!!.size == 0) {
      loginUser()
    }
  }

  private fun loginUser() {
    _state.value = State.LOADING
    auth.loginUser(
      email = email.value!!,
      password = password.value!!,
      onSuccess = {
        _state.value = State.AWAITING
      },
      onFailed = { error ->
        _state.value = State.ENTER_DATA
        _errors.value = mutableListOf(error)
      })
  }

  fun toViewState() {
    _state.value = State.VIEW
  }
}