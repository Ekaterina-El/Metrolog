package el.ka.someapp.data.model.measuring

import java.util.*

data class Verification(
  override var interval: Int = 0,
  override var dateLast: Date? = null,
  override var dateNext: Date? = null,
  var cost: Double = 0.0,
  var place: String = "",
  var verificationCodeCSM: String = "",
) : MeasuringPartRealization