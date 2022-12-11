package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.measuring.*

class PassportViewModel(application: Application) : MeasuringPartViewModel(application) {
  override var measuringPart = MeasuringPart.PASSPORT

  private val _kind = MutableLiveData<MeasuringKind?>(null)
  val measuringKind: LiveData<MeasuringKind?> get() = _kind
  fun setMeasuringKind(newKind: MeasuringKind) {
    _kind.value = newKind
  }

  private val _category = MutableLiveData<MeasuringCategory?>(null)
  val measuringCategory: LiveData<MeasuringCategory?> get() = _category
  fun setCategory(newCategory: MeasuringCategory) {
    _category.value = newCategory
  }

  private val _measurementType = MutableLiveData<MeasurementType?>(null)
  val measurementType: LiveData<MeasurementType?> get() = _measurementType
  fun setMeasurementType(newType: MeasurementType) {
    _measurementType.value = newType
  }

  private val _measurementValues = MutableLiveData<List<MeasurementValue>>(listOf())
  val measurementValue: LiveData<List<MeasurementValue>> get() = _measurementValues
  fun setMeasurementValues(measurementValue: List<MeasurementValue>) {
    _measurementValues.value = measurementValue
  }

  private val _measuringState = MutableLiveData<MeasuringState?>(null)
  val measuringState: LiveData<MeasuringState?> get() = _measuringState
  fun setMeasuringState(newState: MeasuringState) {
    _measuringState.value = newState
  }

  private val _measuringCondition = MutableLiveData<MeasuringCondition?>(null)
  val measuringCondition: LiveData<MeasuringCondition?> get() = _measuringCondition
  fun setMeasuringCondition(newCondition: MeasuringCondition) {
    _measuringCondition.value = newCondition
  }

  val name = MutableLiveData("")
  val type = MutableLiveData("")
  val inventoryNumber = MutableLiveData("")
  val serialNumber = MutableLiveData("")
  val registrationNumberGRSI = MutableLiveData("")
  val manufacturer = MutableLiveData("")
  val supplier = MutableLiveData("")
  val sectorGROEI = MutableLiveData("")


  override fun getMeasuringPartRealization() = MeasuringPassport(
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
    locationIDNode = (measuringPartRealizationOriginal as MeasuringPassport).locationIDNode
  )

  override fun loadMeasuring(
    measuring: Measuring,
    viewerRole: UserRole,
    measuringPartRealization: MeasuringPartRealization
  ) {
    super.loadMeasuring(measuring, viewerRole, measuringPartRealization)
    val passport = measuringPartRealization as MeasuringPassport
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
}