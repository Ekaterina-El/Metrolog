package el.ka.someapp.data.model.measuring

data class MeasurementValue (
  var name: String = "",
  var range: String = "",
  var units: String = "",
  var accuracyClass: String = "",
  var graduationPoint: String = "",
)