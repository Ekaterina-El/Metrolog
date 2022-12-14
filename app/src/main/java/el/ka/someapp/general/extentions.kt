package el.ka.someapp.general

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import el.ka.someapp.data.model.measuring.CategoryHistory
import el.ka.someapp.data.model.measuring.MeasuringHistoryItemExecuted
import java.text.SimpleDateFormat
import java.util.*

val local: Locale get() = Locale.getDefault()
val sdf = SimpleDateFormat("dd/MM/yyyy", local)
val sdfTitle = SimpleDateFormat("dd MMMM, EE", local)
val sdfOnlyTime = SimpleDateFormat("HH:mm", local)

enum class DateConvertType {
  SIMPLE, TITLE, ONLY_TIME
}

fun Date.convertDate(type: DateConvertType = DateConvertType.SIMPLE): String {
  return when (type) {
    DateConvertType.SIMPLE -> sdf.format(this)
    DateConvertType.TITLE -> sdfTitle.format(this)
    DateConvertType.ONLY_TIME -> sdfOnlyTime.format(this)
  }
}

//fun MeasuringPartRealization.toStringDates(): String {
//  val last = this.dateLast?.convertDate() ?: "??/??/????"
//  val next = this.dateNext?.convertDate() ?: "??/??/????"
//  return "$last - $next"
//}

fun String.convertDate(): Date {
  return sdf.parse(this) as Date
}

fun Spinner.addListener(onSelected: (Any?) -> Unit) {
  this.onItemSelectedListener =
    object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        onSelected(p0!!.selectedItem)
      }

      override fun onNothingSelected(p0: AdapterView<*>?) {}
    }
}

fun List<MeasuringHistoryItemExecuted>.byCategory(): List<CategoryHistory> {
  val list = this.toMutableList()

  return list.map { it.history.date!!.convertDate() }.distinct().map { date ->
    val actions = list.filter { it.history.date!!.convertDate() == date }
    val category = CategoryHistory(date.convertDate(), actions)
    list.removeAll(actions)
    return@map category
  }
}
