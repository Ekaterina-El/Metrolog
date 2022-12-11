package el.ka.someapp.data.model.measuring

import java.util.*

data class Certification(
  var place: String = "",
  override var interval: Int = 0,
  override var dateLast: Date? = null,
  override var dateNext: Date? = null,
) : MeasuringPartRealization
