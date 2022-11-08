package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import el.ka.someapp.data.model.convertDate
import el.ka.someapp.data.model.measuring.*
import java.util.*

class AddMeasuringViewModel(application: Application) : AndroidViewModel(application) {
  val name = MutableLiveData("")

  private val _type = MutableLiveData<MeasuringType?>(null)
  fun setType(newType: MeasuringType) {
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

  private val _measuringCondition = MutableLiveData<MeasuringCondition?>(null)
  fun setMeasuringCondition(newCondition: MeasuringCondition) {
    _measuringCondition.value = newCondition
  }

  private val _conditionDate = MutableLiveData<Date?>(null)
  val conditionDate = Transformations.map(_conditionDate) { date ->
    return@map date?.convertDate() ?: ""
  }

  fun setConditionDate(newDate: Date) {
    _conditionDate.value = newDate
  }

  private val _releaseDate = MutableLiveData<Date?>(null)
  val releaseDate = Transformations.map(_releaseDate) { date ->
    return@map date?.convertDate() ?: ""
  }

  fun setReleaseDate(newDate: Date) {
    _releaseDate.value = newDate
  }

  private val _commissioningDate = MutableLiveData<Date?>(null)
  val commissioningDate = Transformations.map(_commissioningDate) { date ->
    return@map date?.convertDate() ?: ""
  }

  fun setCommissioningDate(newDate: Date) {
    _commissioningDate.value = newDate
  }

  private val _locationIDNode = MutableLiveData<String>("")
  fun setLocationIDNode(idNode: String) {
    _locationIDNode.value = idNode
  }

  fun getMessingPassport() {
    _type.value = MeasuringType.MEASURE
    _category.value = MeasuringCategory.WORKING
    _measurementType.value = MeasurementType.ACOUSTIC_QUANTITIES
    _measuringState.value = MeasuringState.CALIBRATION
    _measuringCondition.value = MeasuringCondition.IN_WORK

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