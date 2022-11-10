package el.ka.someapp.data.model.measuring

import java.util.Date

data class MaintenanceRepair(
   var interval: Int = 1,
  var dateLast: Date? = null,
  var dateNext: Date? = null,
  var place: String = "",
  var laboratory: String = ""
)