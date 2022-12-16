package el.ka.someapp.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import el.ka.someapp.R
import el.ka.someapp.data.model.ExportType

class ExportDialog(context: Context) : Dialog(context) {
  private val cboxInformation by lazy { findViewById<CheckBox>(R.id.checkboxInformation) }
  private val cboxMaintenanceRepair by lazy { findViewById<CheckBox>(R.id.checkboxMaintenanceRepair) }
  private val cboxTO by lazy { findViewById<CheckBox>(R.id.checkboxTO) }
  private val cboxOverhaul by lazy { findViewById<CheckBox>(R.id.checkboxOverhaul) }
  private val cboxCalibration by lazy { findViewById<CheckBox>(R.id.checkboxCalibration) }
  private val cboxCertification by lazy { findViewById<CheckBox>(R.id.checkboxCertification) }
  private val cboxVerification by lazy { findViewById<CheckBox>(R.id.checkboxVerification) }

  init {
    initDialog()
  }

  private fun initDialog() {
    setContentView(R.layout.export_dialog)
    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    window!!.setWindowAnimations(R.style.Slide)
    setCancelable(true)
  }

  fun openConfirmDialog(confirmListener: DialogListener, measuringNames: List<String>) {
//    textViewCountMeasuring.text =
//      context.getString(R.string.count_export_measuring, countOfMeasuring)

    val listWrapper = findViewById<LinearLayout>(R.id.measuring_names_list)
    showMeasuringNames(listWrapper, measuringNames)

    findViewById<Button>(R.id.buttonContinue).setOnClickListener {
      confirmListener.onContinue(
        getActiveReports()
      )
    }

    cboxInformation.isChecked = false
    cboxMaintenanceRepair.isChecked = false
    cboxTO.isChecked = false
    cboxOverhaul.isChecked = false
    cboxCalibration.isChecked = false
    cboxCertification.isChecked = false
    cboxVerification.isChecked = false

    show()
  }

  private fun showMeasuringNames(parent: LinearLayout, measuringNames: List<String>) {
    parent.removeAllViewsInLayout()
    measuringNames.forEach {
      parent.addView(createMeasuringNameItem(it))
    }
  }

  private fun createMeasuringNameItem(measuringName: String): TextView {
    val textView = TextView(context, null, 0, R.style.text_items)
    textView.text = context.getString(R.string.list_item, measuringName)
    return textView
  }


  private fun getActiveReports(): List<ExportType> {
    val reports = mutableListOf<ExportType>()
    if (!isShowing) return reports

    if (cboxInformation.isChecked) reports.add(ExportType.GENERAL)
    if (cboxMaintenanceRepair.isChecked) reports.add(ExportType.MAINTENANCE_REPAIR)
    if (cboxTO.isChecked) reports.add(ExportType.TO)
    if (cboxOverhaul.isChecked) reports.add(ExportType.OVERHAUL)
    if (cboxCalibration.isChecked) reports.add(ExportType.CALIBRATION)
    if (cboxCertification.isChecked) reports.add(ExportType.CERTIFICATION)
    if (cboxVerification.isChecked) reports.add(ExportType.VERIFICATION)

    return reports
  }

  fun closeConfirmDialog() {
    dismiss()
  }

  companion object {
    interface DialogListener {
      fun onContinue(exportTypes: List<ExportType>)
    }

    private var dialog: ExportDialog? = null
    fun getInstance(context: Context): ExportDialog {
      dialog = if (dialog == null) ExportDialog(context) else dialog
      return dialog!!
    }
  }
}