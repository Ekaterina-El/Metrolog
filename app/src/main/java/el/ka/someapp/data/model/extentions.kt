package el.ka.someapp.data.model

import java.text.SimpleDateFormat
import java.util.*

fun Date.convertDate(): String {
  val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
  return sdf.format(date)
}