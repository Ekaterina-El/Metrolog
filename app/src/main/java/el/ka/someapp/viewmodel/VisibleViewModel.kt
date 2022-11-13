package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class VisibleViewModel(application: Application) : AndroidViewModel(application) {
  private val _showingNodeNavigation = MutableLiveData(true)
  val showingNodeNavigation: LiveData<Boolean> get() = _showingNodeNavigation
  fun setNodeNavigationState(state: Boolean) { _showingNodeNavigation.value = state }
}