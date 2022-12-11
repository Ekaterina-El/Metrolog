package el.ka.someapp.data.model.measuring

import java.util.*

interface MeasuringPartRealization {
  var interval: Int
  var dateLast: Date?
  var dateNext: Date?
}