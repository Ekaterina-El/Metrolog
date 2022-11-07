package el.ka.someapp.data.model.measuring

import java.util.Date

data class MeasuringPassport(
  var passportId: String = "",
  var type: MeasuringType = MeasuringType.MEASURE,
  var category: MeasuringCategory = MeasuringCategory.WORKING,
  var name: String = "",
  var inventoryNumber: String = "",
  var serialNumber: String = "",
  var registrationNumberGRSI: String = "",
  var manufacturer: String = "",
  var supplier: String = "",
  var sectorGROEI: String = "",
  var measurementType: MeasurementType = MeasurementType.GEOMETRIC_QUANTITIES,
  var range: String = "",
  var accuracyСlass: String = "",
  var graduationPoint: String = "",
  var status: MeasuringState = MeasuringState.CALIBRATION,
  var condition: MeasuringCondition = MeasuringCondition.IN_WORK,
  var conditionDate: Date? = null,
  var releaseDate: Date? = null,
  var commissioningDate: Date? = null,
  var locationIDNode: String = ""
)