package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import el.ka.someapp.data.model.convertDate
import el.ka.someapp.data.model.measuring.*
import java.util.*

class AddMeasuringViewModel(application: Application) : AndroidViewModel(application) {
  val name = MutableLiveData("")

  private val _type = MutableLiveData<MeasuringType?>(null)
  fun setMeasuringType(newType: MeasuringType) {
    _type.value = newType
  }

  private val _category = MutableLiveData<MeasuringCategory?>(null)
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
  fun setMeasurementType(newType: MeasurementType) {
    _measurementType.value = newType
  }

  val range = MutableLiveData("")
  val accuracyСlass = MutableLiveData("")
  val graduationPoint = MutableLiveData("")

  private val _measuringState = MutableLiveData<MeasuringState?>(null)
  fun setMeasuringState(newState: MeasuringState) {
    _measuringState.value = newState
  }

  private val _editDate = MutableLiveData<DateType?>(null)
  fun setEditTime(timeType: DateType) {
    _editDate.value = timeType
  }

  private val _measuringCondition = MutableLiveData<MeasuringCondition?>(null)
  fun setMeasuringCondition(newCondition: MeasuringCondition) {
    _measuringCondition.value = newCondition
  }

  // region Condition Date
  private val _conditionDate = MutableLiveData<Date?>(null)
  val conditionDate: LiveData<Date?>
    get() = _conditionDate
  val conditionDateFormatted =
    Transformations.map(_conditionDate) { date -> return@map date?.convertDate() ?: "??/??/????" }

  fun setConditionDate(newDate: Date) {
    _conditionDate.value = newDate
  }
  // endregion

  // region Release Date
  private val _releaseDate = MutableLiveData<Date?>(null)
  val releaseDate: LiveData<Date?>
    get() = _releaseDate

  val releaseDateFormatted = Transformations.map(_releaseDate) { date ->
    return@map date?.convertDate() ?: "??/??/????"
  }
  fun setReleaseDate(newDate: Date) {
    _releaseDate.value = newDate
  }
  // endregion

  // region Commissioning Date
  private val _commissioningDate = MutableLiveData<Date?>(null)
  val commissioningDate: LiveData<Date?>
    get() = _commissioningDate
  val commissioningDateFormatted = Transformations.map(_commissioningDate) { date ->
    return@map date?.convertDate() ?: "??/??/????"
  }

  fun setCommissioningDate(newDate: Date) {
    _commissioningDate.value = newDate
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


  private val _locationIDNode = MutableLiveData<String>("")
  fun setLocationIDNode(idNode: String) {
    _locationIDNode.value = idNode
  }

  fun getMessingPassport() {
    /*
    _type.value = MeasuringType.MEASURE
    _category.value = MeasuringCategory.WORKING
    _measurementType.value = MeasurementType.ACOUSTIC_QUANTITIES
    _measuringState.value = MeasuringState.CALIBRATION
    _measuringCondition.value = MeasuringCondition.IN_WORK
    */

    val messingPassport = MeasuringPassport(
      passportId = "",
      type = _type.value!!,
      category = _category.value!!,
      name = name.value!!,
      inventoryNumber = inventoryNumber.value,
      serialNumber = serialNumber.value,
      registrationNumberGRSI = registrationNumberGRSI.value,
      manufacturer = manufacturer.value,
      supplier = supplier.value,
      sectorGROEI = sectorGROEI.value,
      measurementType = _measurementType.value!!,
      range = range.value!!,
      accuracyСlass = accuracyСlass.value!!,
      graduationPoint = graduationPoint.value!!,
      status = _measuringState.value!!,
      condition = _measuringCondition.value!!,
      conditionDate = _conditionDate.value,
      releaseDate = _releaseDate.value,
      commissioningDate = _commissioningDate.value,
      locationIDNode = _locationIDNode.value!!
    )
    val a = 10
  }
}