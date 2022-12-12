package el.ka.someapp.data.model.measuring

import java.util.*

data class MeasuringHistoryItem(
  var date: Date? = null,
  var userId: String = "",
  var action: MeasuringActionType = MeasuringActionType.CREATED,
)