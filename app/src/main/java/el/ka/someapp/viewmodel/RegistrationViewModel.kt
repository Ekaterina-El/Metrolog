package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.State

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
  val email = MutableLiveData("")
  val fullName = MutableLiveData("")
  val password = MutableLiveData("")
  val repeatPassword = MutableLiveData("")

  private val _state = MutableLiveData(State.ENTER_DATA)
  val state: LiveData<State>
    get() = _state

  fun verificationCredentials() {
    //Verifications...
    _state.value = State.LOADING
  }
}