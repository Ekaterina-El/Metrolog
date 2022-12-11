package el.ka.someapp.data.model.measuring

import java.util.*

data class MaintenanceRepair(
  override var interval: Int = 0,
  override var dateLast: Date? = null,
  override var dateNext: Date? = null,
  var place: String = "",
  var laboratory: String = ""
) : MeasuringPartRealization