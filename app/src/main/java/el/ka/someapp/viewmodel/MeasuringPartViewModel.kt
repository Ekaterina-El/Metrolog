package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.measuring.DateType
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.data.model.measuring.MeasuringPart
import el.ka.someapp.data.model.measuring.MeasuringPartRealization
import el.ka.someapp.data.repository.MeasuringDatabaseService
import el.ka.someapp.general.convertDate
import java.util.*
import java.util.concurrent.TimeUnit

abstract class MeasuringPartViewModel(application: Application) : AndroidViewModel(application) {
  private lateinit var measuring: Measuring
  lateinit var measuringPartRealizationOriginal: MeasuringPartRealization

  abstract var measuringPart: MeasuringPart

  private val _state = MutableLiveData(State.VIEW)
  val state: LiveData<State> get() = _state

  private val _viewerRole = MutableLiveData<UserRole?>()
  val viewerRole: LiveData<UserRole?> get() = _viewerRole

  fun saveMeasuring(after: (MeasuringPartRealization) -> Unit) {
    _state.value = State.LOADING
    val newValue = getMeasuringPartRealization()

    // Если изменния не былои внесены - вернутся назад
    if (newValue == measuringPartRealizationOriginal) {
      _state.value = State.BACK
      return
    }

    // Сохранение изменнений на сервере
    MeasuringDatabaseService.updateMeasuringPart(
      measuringID = measuring.measuringID,
      part = measuringPart,
      value = newValue,
      onFailure = {
        _state.value = State.VIEW
      },
      onSuccess = {
        after(newValue)
        _state.value = State.BACK
      }
    )
  }

  abstract fun getMeasuringPartRealization(): MeasuringPartRealization

  open fun loadMeasuring(
    measuring: Measuring,
    viewerRole: UserRole,
    measuringPartRealization: MeasuringPartRealization
  ) {
    _viewerRole.value = viewerRole

    this@MeasuringPartViewModel.measuring = measuring
    this@MeasuringPartViewModel.measuringPartRealizationOriginal = measuringPartRealization

    _lastDate.value = measuringPartRealization.dateLast
    _nextDate.value = measuringPartRealization.dateNext
    _interval.value = measuringPartRealization.interval
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
      DateType.RELEASE -> _releaseDate.value = date
      DateType.CONDITION -> _conditionDate.value = date
      DateType.COMMISSION -> _commissioningDate.value = date
      else -> {}
    }
    if (_editDate.value == DateType.LAST || _editDate.value == DateType.NEXT) updateInterval()
  }


  // region Condition Date
  val _conditionDate = MutableLiveData<Date?>(null)
  val conditionDate: LiveData<Date?>
    get() = _conditionDate
  val conditionDateFormatted =
    Transformations.map(_conditionDate) { date ->
      return@map date?.convertDate() ?: "??/??/????"
    }
  // endregion

  // region Release Date
  val _releaseDate = MutableLiveData<Date?>(null)
  val releaseDate: LiveData<Date?>
    get() = _releaseDate

  val releaseDateFormatted = Transformations.map(_releaseDate) { date ->
    return@map date?.convertDate() ?: "??/??/????"
  }
  // endregion

  // region Commissioning Date
  val _commissioningDate = MutableLiveData<Date?>(null)
  val commissioningDate: LiveData<Date?>
    get() = _commissioningDate
  val commissioningDateFormatted = Transformations.map(_commissioningDate) { date ->
    return@map date?.convertDate() ?: "??/??/????"
  }
  // endregion

  val _lastDate = MutableLiveData<Date?>(null)
  val lastDate: LiveData<Date?>
    get() = _lastDate
  val lastDateFormatted =
    Transformations.map(_lastDate) { date ->
      return@map date?.convertDate() ?: "??/??/????"
    }

  val _nextDate = MutableLiveData<Date?>(null)
  val nextDate: LiveData<Date?>
    get() = _nextDate
  val nextDateFormatted =
    Transformations.map(_nextDate) { date ->
      return@map date?.convertDate() ?: "??/??/????"
    }
  // endregion

  val _interval = MutableLiveData<Int>()
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
}