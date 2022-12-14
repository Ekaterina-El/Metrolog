package el.ka.someapp.data.model.measuring

data class Measuring(
  var measuringID: String = "",
  var passport: MeasuringPassport = MeasuringPassport(),
  var verification: Verification = Verification(),
  var TO: TO = TO(),
  var maintenanceRepair: MaintenanceRepair = MaintenanceRepair(),
  var overhaul: Overhaul = Overhaul(),
  var certification: Certification = Certification(),
  var calibration: Calibration = Calibration(),
  var history: List<MeasuringHistoryItem> = listOf()
)