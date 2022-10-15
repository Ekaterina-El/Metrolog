package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.User
import el.ka.someapp.data.repository.AuthenticationService
import el.ka.someapp.utils.Verificator

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
  val auth = AuthenticationService
  val email = MutableLiveData("")
  val fullName = MutableLiveData("")
  val password = MutableLiveData("")
  val repeatPassword = MutableLiveData("")

  private val _errors = MutableLiveData(mutableListOf<ErrorApp>())
  val errors: MutableLiveData<MutableList<ErrorApp>>
    get() = _errors


  private val _state = MutableLiveData(State.ENTER_DATA)
  val state: LiveData<State>
    get() = _state


  fun verificationCredentials() {
    _errors.value = Verificator.checkForRegistration(
      email = email.value!!,
      fullName = fullName.value!!,
      password = password.value!!,
      repeatPassword = repeatPassword.value!!
    ).toMutableList()

    if (_errors.value!!.size == 0) { registrationUser() }
  }

  private fun registrationUser() {
    _state.value = State.LOADING
    auth.registerUser(
      password = password.value!!,
      onSuccess = {
        _state.value = State.AWAITING
      },
      onFailure = { error ->
        _state.value = State.ENTER_DATA
        _errors.value = mutableListOf(error)
      },
      userData = User(
        fullName = fullName.value!!,
        email = email.value!!
      )
    )
  }

}