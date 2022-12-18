package el.ka.someapp.data.model.measuring

data class MeasuringReport(
  val nodeName: String,
  val companyName: String,
  val countOfOverdue: Int = 0,
  val countOfHalfOverdue: Int = 0
)