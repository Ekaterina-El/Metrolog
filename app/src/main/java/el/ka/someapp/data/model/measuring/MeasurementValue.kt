package el.ka.someapp.data.model.measuring

data class MeasurementValue (
  var name: String = "",
  var rangeMin: String = "",
  var rangeMax: String = "",
  var units: String = "",
  var accuracyClass: String = "",
  var graduationPoint: String = "",
)