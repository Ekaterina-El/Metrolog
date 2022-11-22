package el.ka.someapp.data.model.measuring

import java.util.Date

data class Verification(
  var interval: Int = 0,
  var dateLast: Date? = null,
  var dateNext: Date? = null,
  var cost: Double = 0.0,
  var place: String = "",
  var verificationCodeCSM: String = "",
)