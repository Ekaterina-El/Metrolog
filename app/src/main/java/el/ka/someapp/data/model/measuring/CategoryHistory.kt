package el.ka.someapp.data.model.measuring

import java.util.*

data class CategoryHistory(
  val date: Date,
  val actions: List<MeasuringHistoryItemExecuted>
)