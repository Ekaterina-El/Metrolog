package el.ka.someapp.data.model.measuring

import java.util.Date

data class MeasuringPassport(
  var kind: MeasuringKind = MeasuringKind.MEASURE,
  var category: MeasuringCategory = MeasuringCategory.WORKING,
  var name: String = "",
  val type: String = "",
  var inventoryNumber: String? = "",
  var serialNumber: String? = "",
  var registrationNumberGRSI: String? = "",
  var manufacturer: String? = "",
  var supplier: String? = "",
  var sectorGROEI: String? = "",
  var measurementType: MeasurementType = MeasurementType.GEOMETRIC_QUANTITIES,
  var measurementValues: List<MeasurementValue> = listOf(),
  var status: MeasuringState = MeasuringState.CALIBRATION,
  var condition: MeasuringCondition = MeasuringCondition.IN_WORK,
  var conditionDate: Date? = null,
  var releaseDate: Date? = null,
  var commissioningDate: Date? = null,
  var locationIDNode: String = ""
)