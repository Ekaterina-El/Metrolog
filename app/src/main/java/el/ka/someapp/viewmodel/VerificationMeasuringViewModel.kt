package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.convertDate
import el.ka.someapp.data.model.measuring.*
import el.ka.someapp.data.repository.MeasuringDatabaseService
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.round

class VerificationMeasuringViewModel(application: Application) : AndroidViewModel(application) {
  private lateinit var measuring: Measuring
  private lateinit var verification: Verification

  fun saveMeasuring(after: (Verification) -> Unit) {
    _state.value = State.LOADING
    val newVerification = getVerification()

    // Если изменния не былои внесены - вернутся назад
    if (newVerification == verification) {
      _state.value = State.BACK
      return
    }

    // Сохранение изменнений на сервере
    MeasuringDatabaseService.updateMeasuringPart(
      measuringID = measuring.measuringID,
      part = MeasuringPart.VERIFICATION,
      value = newVerification,
      onFailure = {
        _state.value = State.VIEW
      },
      onSuccess = {
        after(newVerification)
        _state.value = State.BACK
      }
    )
  }

  private fun getVerification() = Verification(
    interval = _interval.value!!,
    dateLast = _lastDate.value!!,
    dateNext = _nextDate.value!!,
    place = place.value!!,
    verificationCodeCSM = verificationCodeCSM.value!!,
    cost = getRoundedCost()
  )

  private fun getRoundedCost(): Double {
    val a = cost.value!!.toDouble()
    return round(a * 100) / 100
  }

  fun loadMeasuring(measuring: Measuring, viewerRole: UserRole) {
    this@VerificationMeasuringViewModel.measuring = measuring
    this@VerificationMeasuringViewModel.verification = measuring.verification

    _viewerRole.value = viewerRole

    val verification = measuring.verification
    _lastDate.value = verification.dateLast
    _nextDate.value = verification.dateNext
    _interval.value = verification.interval
    place.value = verification.place
    verificationCodeCSM.value = verification.verificationCodeCSM
    cost.value = verification.cost.toString()
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

  val verificationCodeCSM = MutableLiveData<String>()
  val cost = MutableLiveData<String>()
  val place = MutableLiveData<String>()


  private val _viewerRole = MutableLiveData<UserRole?>()
  val viewerRole: LiveData<UserRole?> get() = _viewerRole
}