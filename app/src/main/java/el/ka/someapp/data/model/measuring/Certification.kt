package el.ka.someapp.data.model.measuring

import java.util.*

data class Certification(
  var interval: Int = 0,
  var dateLast: Date? = null,
  var dateNext: Date? = null,
  var place: String = "",
)