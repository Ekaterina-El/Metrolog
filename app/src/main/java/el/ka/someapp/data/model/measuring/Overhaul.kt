package el.ka.someapp.data.model.measuring

import java.util.Date

data class Overhaul(
  var interval: Int = 0,
  var dateLast: Date? = null,
  var dateNext: Date? = null,
  var place: String = "",
  var laboratory: String = ""
)