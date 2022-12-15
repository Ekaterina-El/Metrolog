package el.ka.someapp.general

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import el.ka.someapp.data.model.ExportType
import el.ka.someapp.data.model.measuring.CategoryHistory
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.data.model.measuring.MeasuringHistoryItemExecuted
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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

fun Date.daysTo(date: Date): Int {
  val current = this.time
  val timeDateTo = date.time
  val diff = timeDateTo - current
  return TimeUnit.MILLISECONDS.toDays(diff).toInt()
}

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


fun List<Measuring>.toRowsForExport(
  context: Context,
  companyName: String,
  exportType: ExportType
): List<List<String>> = this.map {
  val list = mutableListOf<String>()

  val pass = it.passport
  list.add(pass.name)
  list.add(pass.type)
  list.add(pass.serialNumber.toString())
  list.add(pass.inventoryNumber.toString())

  if (companyName != pass.locationNodeName) list.add("$companyName, ${pass.locationNodeName}")
  else list.add(companyName)

  when (exportType) {
    ExportType.GENERAL -> {
      list.add(context.getString(pass.status.strRes))
      list.add(context.getString(pass.condition.strRes))
    }

    ExportType.OVERHAUL -> {
      list.add(it.overhaul.dateLast?.convertDate() ?: "??/??/????")
      list.add(it.overhaul.dateNext?.convertDate() ?: "??/??/????")
      list.add(it.overhaul.place + " " + it.overhaul.laboratory)
    }

    ExportType.MAINTENANCE_REPAIR -> {
      list.add(it.maintenanceRepair.dateLast?.convertDate() ?: "??/??/????")
      list.add(it.maintenanceRepair.dateNext?.convertDate() ?: "??/??/????")
      list.add(it.maintenanceRepair.place + " " + it.maintenanceRepair.laboratory)
    }

    ExportType.TO -> {
      list.add(it.TO.dateLast?.convertDate() ?: "??/??/????")
      list.add(it.TO.dateNext?.convertDate() ?: "??/??/????")
    }

    ExportType.VERIFICATION -> {
      list.add(it.verification.dateLast?.convertDate() ?: "??/??/????")
      list.add(it.verification.dateNext?.convertDate() ?: "??/??/????")
    }

    ExportType.CALIBRATION -> {
      list.add(it.calibration.dateLast?.convertDate() ?: "??/??/????")
      list.add(it.calibration.dateNext?.convertDate() ?: "??/??/????")
    }

    ExportType.CERTIFICATION -> {
      list.add(it.certification.dateLast?.convertDate() ?: "??/??/????")
      list.add(it.certification.dateNext?.convertDate() ?: "??/??/????")
    }
  }

  return@map list
}