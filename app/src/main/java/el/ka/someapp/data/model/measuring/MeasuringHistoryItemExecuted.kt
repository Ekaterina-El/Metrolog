package el.ka.someapp.data.model.measuring

import el.ka.someapp.data.model.User

data class MeasuringHistoryItemExecuted(
  val user: User,
  val history: MeasuringHistoryItem
)
