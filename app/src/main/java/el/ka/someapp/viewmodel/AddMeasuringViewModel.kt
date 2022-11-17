package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.convertDate
import el.ka.someapp.data.model.measuring.*
import el.ka.someapp.data.repository.MeasuringDatabaseService
import java.util.*

class AddMeasuringViewModel(application: Application) : AndroidViewModel(application) {
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
    Transformations.map(_conditionDate) { date -> return@map date?.convertDate() ?: "??/??/????" }
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


  private val _locationIDNode = MutableLiveData("")
  fun setLocationIDNode(idNode: String) {
    _locationIDNode.value = idNode
  }

  fun saveMeasuring(after: (String) -> Unit) {
    _state.value = State.LOADING
    val measuringPassport = getMessingPassport()
    val measuring = Measuring(passport = measuringPassport)

    MeasuringDatabaseService.createMeasuring(
      measuring = measuring,
      onSuccess = {
        after(it)
        _state.value = State.MEASURING_ADDED
      },
      onFailure = {
        _state.value = State.VIEW
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
      locationIDNode = _locationIDNode.value!!
    )
  }

  fun goBack() {
    clearAllFields()
    _state.value = State.BACK
  }

  private fun clearAllFields() {
    name.value = ""
    _kind.value = null
    _category.value = null
    inventoryNumber.value = null
    serialNumber.value = null
    registrationNumberGRSI.value = null
    manufacturer.value = null
    supplier.value = null
    _measurementValues.value = listOf()
    _measuringState.value = null
    _measuringCondition.value = null
    _conditionDate.value = null
    _releaseDate.value = null
    _commissioningDate.value = null
    _locationIDNode.value = null
  }
}