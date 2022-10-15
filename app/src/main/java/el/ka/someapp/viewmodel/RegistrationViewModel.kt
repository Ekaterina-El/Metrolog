package el.ka.someapp.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.ErrorApp
import el.ka.someapp.data.Errors
import el.ka.someapp.data.State

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
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
    //_state.value = State.LOADING
    _errors.value  = checkCredentials()

  }

  private fun checkCredentials(): MutableList<ErrorApp> {
    val listOfErrors = mutableListOf<ErrorApp>()
    checkEmail(listOfErrors)
    checkName(listOfErrors)
    checkPassword(listOfErrors)

    return listOfErrors
  }

  private fun checkPassword(listOfErrors: MutableList<ErrorApp>) {
    val value = password.value
    val repeat = repeatPassword.value
    if (value.isNullOrEmpty() || value.length < 8) {
      listOfErrors.add(Errors.invalidPassword)
    } else if (value != repeat) {
      listOfErrors.add(Errors.noEqualPassword)
    }
    listOfErrors.add(Errors.invalidPassword)
    listOfErrors.add(Errors.invalidPassword)
    listOfErrors.add(Errors.invalidPassword)
    listOfErrors.add(Errors.invalidPassword)

  }

  private fun checkName(listOfErrors: MutableList<ErrorApp>) {
    val value = fullName.value
    if (value.isNullOrEmpty() || value.trim().split(" ").size < 2) {
      listOfErrors.add(Errors.invalidFullName)
    }
  }

  private fun checkEmail(listOfErrors: MutableList<ErrorApp>) {
    val value = email.value
    if (value.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
      listOfErrors.add(Errors.invalidEmail)
    }
    // TODO: проверка на статус свободности почты...
  }

}