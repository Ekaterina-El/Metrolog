package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.measuring.DateType
import el.ka.someapp.data.model.measuring.MaintenanceRepair
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.data.model.measuring.MeasuringPart
import el.ka.someapp.data.repository.MeasuringDatabaseService
import el.ka.someapp.general.convertDate
import java.util.*
import java.util.concurrent.TimeUnit

class MaintenanceRepairViewModel(application: Application) : AndroidViewModel(application) {
  private lateinit var measuring: Measuring
  private lateinit var maintenanceRepair: MaintenanceRepair

  fun saveMeasuring(after: (MaintenanceRepair) -> Unit) {
    _state.value = State.LOADING
    val newMaintenanceRepair = getMaintenanceRepair()

    // Если изменния не былои внесены - вернутся назад
    if (newMaintenanceRepair == maintenanceRepair) {
      _state.value = State.BACK
      return
    }

    // Сохранение изменнений на сервере
    MeasuringDatabaseService.updateMeasuringPart(
      measuringID = measuring.measuringID,
      part = MeasuringPart.MAINTENANCE_REPAIR,
      value = newMaintenanceRepair,
      onFailure = {
        _state.value = State.VIEW
      },
      onSuccess = {
        after(newMaintenanceRepair)
        _state.value = State.BACK
      }
    )
  }

  private fun getMaintenanceRepair() = MaintenanceRepair(
    interval = _interval.value!!,
    dateLast = _lastDate.value!!,
    dateNext = _nextDate.value!!,
    place = place.value!!,
    laboratory = laboratory.value!!
  )

  fun loadMeasuring(measuring: Measuring, viewerRole: UserRole) {
    this@MaintenanceRepairViewModel.measuring = measuring
    this@MaintenanceRepairViewModel.maintenanceRepair = measuring.maintenanceRepair

    _viewerRole.value = viewerRole

    val maintenanceRepair = measuring.maintenanceRepair
    _lastDate.value = maintenanceRepair.dateLast
    _nextDate.value = maintenanceRepair.dateNext
    _interval.value = maintenanceRepair.interval
    place.value = maintenanceRepair.place
    laboratory.value = maintenanceRepair.laboratory
  }

  // region Date Pickers
  private val _editDate = MutableLiveData<DateType?>(null)
  fun setEditTime(timeType: DateType) {
    _editDate.value = timeType
  }

  fun saveDate(date: Date) {
    when (_editDate.value) {
      DateType.LAST -> _lastDate.value = date
      DateType.NEXT -> _nextDate.value = date
      else -> {}
    }
    if (_editDate.value == DateType.LAST || _editDate.value == DateType.NEXT) updateInterval()
  }
  // endregion

  private val _state = MutableLiveData(State.VIEW)
  val state: LiveData<State> get() = _state

  // region Condition Date
  private val _lastDate = MutableLiveData<Date?>(null)
  val lastDate: LiveData<Date?>
    get() = _lastDate
  val lastDateFormatted =
    Transformations.map(_lastDate) { date ->
      return@map date?.convertDate() ?: "??/??/????"
    }
  // endregion

  // region Condition Date
  private val _nextDate = MutableLiveData<Date?>(null)
  val nextDate: LiveData<Date?>
    get() = _nextDate
  val nextDateFormatted =
    Transformations.map(_nextDate) { date ->
      return@map date?.convertDate() ?: "??/??/????"
    }
  // endregion


  // region Interval
  private val _interval = MutableLiveData<Int>()
  val interval: LiveData<Int> get() = _interval

  private fun updateInterval() {
    val last = lastDate.value?.time
    val next = nextDate.value?.time

    if (last != null && next != null) {
      val diff = next - last
      val days = TimeUnit.MILLISECONDS.toDays(diff).toInt()
      _interval.value = days
    }
  }
  // region

  val laboratory = MutableLiveData<String>()
  val place = MutableLiveData<String>()

  private val _viewerRole = MutableLiveData<UserRole?>()
  val viewerRole: LiveData<UserRole?> get() = _viewerRole
}