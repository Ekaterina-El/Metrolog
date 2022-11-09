package el.ka.someapp.data.model

import java.text.SimpleDateFormat
import java.util.*

fun Date.convertDate(): String {
  val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
  return sdf.format(this)
}