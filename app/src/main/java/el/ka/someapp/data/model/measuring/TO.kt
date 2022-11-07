package el.ka.someapp.data.model.measuring

import java.util.Date

data class TO(
  var TO_ID: String = "",
  var interval: Int = 1,
  var dateLast: Date? = null,
  var dateNext: Date? = null,
)