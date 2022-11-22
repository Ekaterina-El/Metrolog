package el.ka.someapp.viewmodel

import android.app.Application
import android.util.Log
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

class PassportViewModel(application: Application) : AndroidViewModel(application) {
  lateinit var passport: MeasuringPassport
  lateinit var measuring: Measuring

  private val _viewerRole = MutableLiveData<UserRole?>(null)
  val viewerRole: LiveData<UserRole?> get() = _viewerRole

  fun loadMeasuring(measuring: Measuring, viewerRole: UserRole) {
    this@PassportViewModel.measuring = measuring
    passport = measuring.passport!!

    _viewerRole.value = viewerRole

    name.value = passport.name
    _kind.value = passport.kind
    type.value = passport.type
    _category.value = passport.category
    inventoryNumber.value = passport.inventoryNumber
    serialNumber.value = passport.serialNumber
    registrationNumberGRSI.value = passport.registrationNumberGRSI
    manufacturer.value = passport.manufacturer
    supplier.value = passport.supplier
    sectorGROEI.value = passport.sectorGROEI
    _measurementType.value = passport.measurementType
    _measurementValues.value = passport.measurementValues
    _measuringState.value = passport.status
    _measuringCondition.value = passport.condition
    _conditionDate.value = passport.conditionDate
    _releaseDate.value = passport.releaseDate
    _commissioningDate.value = passport.commissioningDate
  }

  private val _state = MutableLiveData(State.VIEW)
  val state: LiveData<State>
    get() = _state

  val name = MutableLiveData("")

  private val _kind = MutableLiveData<MeasuringKind?>(null)
  val measuringKind: LiveData<MeasuringKind?>
    get() = _kind

  fun setMeasuringKind(newKind: MeasuringKind) {
    _kind.value = newKind
  }

  val type = MutableLiveData("")

  private val _category = MutableLiveData<MeasuringCategory?>(null)
  val measuringCategory: LiveData<MeasuringCategory?>
    get() = _category

  fun setCategory(newCategory: MeasuringCategory) {
    _category.value = newCategory
  }

  val inventoryNumber = MutableLiveData("")
  val serialNumber = MutableLiveData("")
  val registrationNumberGRSI = MutableLiveData("")
  val manufacturer = MutableLiveData("")
  val supplier = MutableLiveData("")
  val sectorGROEI = MutableLiveData("")

  private val _measurementType = MutableLiveData<MeasurementType?>(null)
  val measurementType: LiveData<MeasurementType?>
    get() = _measurementType

  fun setMeasurementType(newType: MeasurementType) {
    _measurementType.value = newType
  }

  private val _measurementValues = MutableLiveData<List<MeasurementValue>>(listOf())
  val measurementValue: LiveData<List<MeasurementValue>> get() = _measurementValues

  fun setMeasurementValues(measurementValue: List<MeasurementValue>) {
    _measurementValues.value = measurementValue
  }

  private val _measuringState = MutableLiveData<MeasuringState?>(null)
  val measuringState: LiveData<MeasuringState?>
    get() = _measuringState

  fun setMeasuringState(newState: MeasuringState) {
    _measuringState.value = newState
  }

  private val _measuringCondition = MutableLiveData<MeasuringCondition?>(null)
  val measuringCondition: LiveData<MeasuringCondition?>
    get() = _measuringCondition

  fun setMeasuringCondition(newCondition: MeasuringCondition) {
    _measuringCondition.value = newCondition
  }

  private val _editDate = MutableLiveData<DateType?>(null)
  fun setEditTime(timeType: DateType) {
    _editDate.value = timeType
  }

  // region Condition Date
  private val _conditionDate = MutableLiveData<Date?>(null)
  val conditionDate: LiveData<Date?>
    get() = _conditionDate
  val conditionDateFormatted =
    Transformations.map(_conditionDate) { date ->
      return@map date?.convertDate() ?: "??/??/????"
    }
  // endregion

  // region Release Date
  private val _releaseDate = MutableLiveData<Date?>(null)
  val releaseDate: LiveData<Date?>
    get() = _releaseDate

  val releaseDateFormatted = Transformations.map(_releaseDate) { date ->
    return@map date?.convertDate() ?: "??/??/????"
  }
  // endregion

  // region Commissioning Date
  private val _commissioningDate = MutableLiveData<Date?>(null)
  val commissioningDate: LiveData<Date?>
    get() = _commissioningDate
  val commissioningDateFormatted = Transformations.map(_commissioningDate) { date ->
    return@map date?.convertDate() ?: "??/??/????"
  }
  // endregion

  fun saveDate(date: Date) {
    when (_editDate.value) {
      DateType.RELEASE -> _releaseDate.value = date
      DateType.CONDITION -> _conditionDate.value = date
      DateType.COMMISSION -> _commissioningDate.value = date
      else -> {}
    }
  }

  fun saveMeasuring(after: (MeasuringPassport) -> Unit) {
    _state.value = State.LOADING
    val measuringPassport = getMessingPassport()

    // Если изменния не былои внесены - вернутся назад
    if (measuringPassport == passport) {
      _state.value = State.BACK
      return
    }

    // Сохранение изменнений на сервере
    MeasuringDatabaseService.updateMeasuringPart(
      measuringID = measuring.measuringID,
      part = MeasuringPart.PASSPORT,
      value = measuringPassport,
      onFailure = {
        _state.value = State.VIEW
      },
      onSuccess = {
        after(measuringPassport)
        _state.value = State.BACK
      }
    )
  }

  private fun getMessingPassport(): MeasuringPassport {
    return MeasuringPassport(
      kind = _kind.value!!,
      category = _category.value!!,
      name = name.value!!,
      type = type.value!!,
      inventoryNumber = inventoryNumber.value,
      serialNumber = serialNumber.value,
      registrationNumberGRSI = registrationNumberGRSI.value,
      manufacturer = manufacturer.value,
      supplier = supplier.value,
      sectorGROEI = sectorGROEI.value,
      measurementType = _measurementType.value!!,
      measurementValues = _measurementValues.value!!,
      status = _measuringState.value!!,
      condition = _measuringCondition.value!!,
      conditionDate = _conditionDate.value,
      releaseDate = _releaseDate.value,
      commissioningDate = _commissioningDate.value,
      locationIDNode = passport.locationIDNode
    )
  }
}