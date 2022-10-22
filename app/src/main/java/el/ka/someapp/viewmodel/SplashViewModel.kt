package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.repository.AuthenticationService

class SplashViewModel(application: Application) : AndroidViewModel(application) {
  private val _userIsAuthed = MutableLiveData(UserState.UNKNOWN)
  val userIsAuthed: LiveData<UserState>
    get() = _userIsAuthed

  fun checkUserIsAuth() {
    AuthenticationService.checkUserIsAuth(
      onAuth = { _userIsAuthed.value = UserState.AUTH },
      onNoAuth = { _userIsAuthed.value = UserState.NO_AUTH }
    )
  }
}

enum class UserState {
  UNKNOWN, NO_AUTH, AUTH
}