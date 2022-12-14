package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.*
import el.ka.someapp.data.repository.NodesDatabaseService

class JobFieldViewModel(application: Application) : AndroidViewModel(application) {
  val jobFieldName = MutableLiveData("")

  val role = MutableLiveData<UserRole?>(null)

  private val _selectedUser = MutableLiveData<User?>(null)

  val hasRoleError = MutableLiveData(false)

  fun setRole(userRole: UserRole) {
    role.value = userRole
  }

  private val _state = MutableLiveData(State.VIEW)
  val state: LiveData<State>
    get() = _state

  private val _jobField = MutableLiveData<JobField?>(null)
  val jobField: LiveData<JobField?>
    get() = _jobField

  private val _nameError = MutableLiveData<ErrorApp?>(null)
  val nameError: LiveData<ErrorApp?>
    get() = _nameError

  private fun checkErrors(): Int {
    var errors = 0

    if (role.value == null && _state.value != State.EDIT_JOB_FIELD) {
      errors += 1
      hasRoleError.value = true
    } else {
      hasRoleError.value = false
    }

    if (_state.value != State.EDIT_JOB_FIELD) {
      when (jobFieldName.value) {
        "" -> {
          _nameError.value = Errors.emptyFieldNameValue
          errors += 1
        }
        UserRole.HEAD.roleName -> {
          _nameError.value = Errors.reservedValue
          errors += 1
        }
        else -> {
          _nameError.value = null
        }
      }
    }
    return errors
  }


  private val _oldJobField = MutableLiveData<JobField?>()
  val oldJobField: LiveData<JobField?> get() = _oldJobField

  fun editJobField(nodeId: String) {
    val errors = checkErrors()
    if (errors == 0) {
      _oldJobField.value = _jobField.value
//      _state.value = State.JOB_FIELD_EDITING
      _jobField.value = getJobField()

      NodesDatabaseService.editJobField(
        nodeId,
        _oldJobField.value!!,
        _jobField.value!!,
        onFailure = {}) {
        clearJobField()
        _state.value = State.JOB_FIELD_EDITED
      }
    }
  }

  fun tryCreateJobField(nodeId: String) {
    val errors = checkErrors()

    if (errors == 0) {
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
    _nameError.value = null
    role.value = null
    jobFieldName.value = ""
    _selectedUser.value = null
  }

  fun setJobField(jobField: JobField, user: User) {
    _jobField.value = jobField
    jobFieldName.value = jobField.jobName
    role.value = jobField.jobRole
    _selectedUser.value = user
    _state.value = State.EDIT_JOB_FIELD
  }

  private fun getJobField() = JobField(
    jobName = jobFieldName.value!!,
    jobRole = role.value!!,
    userId = _selectedUser.value!!.uid
  )

  fun afterNotifiedOfNewFieldJob() {
    _state.value = State.VIEW
  }

  fun clearFields() {
    jobFieldName.value = ""
    role.value = null
    _state.value = State.VIEW
    _nameError.value = null
  }

  fun setUser(user: User) {
    _selectedUser.value = user
  }
}