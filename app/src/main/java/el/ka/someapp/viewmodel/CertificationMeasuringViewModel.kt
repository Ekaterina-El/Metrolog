package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.measuring.Certification
import el.ka.someapp.data.model.measuring.DateType
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.data.model.measuring.MeasuringPart
import el.ka.someapp.data.repository.MeasuringDatabaseService
import el.ka.someapp.general.convertDate
import java.util.*
import java.util.concurrent.TimeUnit

class CertificationMeasuringViewModel(application: Application) : AndroidViewModel(application) {
  private lateinit var measuring: Measuring
  private lateinit var certification: Certification

  fun saveMeasuring(after: (Certification) -> Unit) {
    _state.value = State.LOADING
    val newCertification = getCertification()

    // Если изменния не былои внесены - вернутся назад
    if (newCertification == certification) {
      _state.value = State.BACK
      return
    }

    // Сохранение изменнений на сервере
    MeasuringDatabaseService.updateMeasuringPart(
      measuringID = measuring.measuringID,
      part = MeasuringPart.CERTIFICATION,
      value = newCertification,
      onFailure = {
        _state.value = State.VIEW
      },
      onSuccess = {
        after(newCertification)
        _state.value = State.BACK
      }
    )
  }

  private fun getCertification() = Certification(
    interval = _interval.value!!,
    dateLast = _lastDate.value!!,
    dateNext = _nextDate.value!!,
    place = place.value!!,
  )

  fun loadMeasuring(measuring: Measuring, viewerRole: UserRole) {
    val certification = measuring.certification
    _viewerRole.value = viewerRole

    this@CertificationMeasuringViewModel.measuring = measuring
    this@CertificationMeasuringViewModel.certification = certification

    _lastDate.value = certification.dateLast
    _nextDate.value = certification.dateNext
    _interval.value = certification.interval
    place.value = certification.place
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
  val place = MutableLiveData<String>()

  private val _viewerRole = MutableLiveData<UserRole?>()
  val viewerRole: LiveData<UserRole?> get() = _viewerRole
  // endregion
}