package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.JobField
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.User
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.repository.NodesDatabaseService

class JobFieldViewModel(application: Application) : AndroidViewModel(application) {
  val jobFieldName = MutableLiveData("")

  val role = MutableLiveData<UserRole?>(null)

  private val _selectedUser = MutableLiveData<User?>(null)
  val selectedUser: LiveData<User?> = _selectedUser

  val hasNameError = MutableLiveData(false)
  val hasRoleError = MutableLiveData(false)

  fun setRole(userRole: UserRole) {
    role.value = userRole
  }

  private val _state = MutableLiveData<State>(State.VIEW)
  val state: LiveData<State>
    get() = _state

  private val _jobField = MutableLiveData<JobField?>(null)
  val jobField: LiveData<JobField?>
    get() = _jobField

  fun tryCreateJobField(nodeId: String) {
    hasRoleError.value = role.value == null
    hasNameError.value = jobFieldName.value == ""

    if (role.value != null && jobFieldName.value != "") {
      _state.value = State.NEW_FIELD_JOB_ADDING
      _jobField.value = getJobField()

      NodesDatabaseService.addJobField(
        nodeId = nodeId,
        jobField = _jobField.value!!,
        onFailure = {},
        onSuccess = {
          clearJobField()
          _state.value = State.NEW_FIELD_JOB_ADDED
        }
      )
    }
  }

  private fun clearJobField() {
    hasRoleError.value = false
    hasNameError.value = false
    role.value = null
    jobFieldName.value = ""
  }

  fun setUser(user: User) {
    _selectedUser.value = user
  }

  private fun getJobField() = JobField(
    jobName = jobFieldName.value!!,
    jobRole = role.value!!,
    userId = _selectedUser.value!!.uid
  )
}