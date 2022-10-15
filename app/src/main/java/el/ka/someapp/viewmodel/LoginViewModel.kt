package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.State

class LoginViewModel(application: Application) : AndroidViewModel(application) {
  var email = MutableLiveData<String>()
  var password = MutableLiveData<String>()

  private val _state = MutableLiveData(State.ENTER_DATA)
  val state: LiveData<State>
    get() = _state

  init {
    email.value = ""
    password.value = ""
  }

  fun verificationCredentials() {
    // Verification...
    _state.value = State.LOADING
  }
}