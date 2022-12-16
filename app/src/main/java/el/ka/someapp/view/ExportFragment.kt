package el.ka.someapp.view

import el.ka.someapp.MainActivity
import el.ka.someapp.R
import el.ka.someapp.data.model.ExportType
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.view.dialog.ExportDialog

abstract class ExportFragment : BaseFragment() {
  private var exportDialog: ExportDialog? = null
  private val exportDialogListener = object : ExportDialog.Companion.DialogListener {
    override fun onContinue(
      exportTypes: List<ExportType>,
      measuring: List<Measuring>,
      companyName: String
    ) {
      if (exportTypes.isEmpty()) noCheckedExportTypes() else startExport(
        exportTypes,
        measuring,
        companyName
      )
    }
  }

  private fun startExport(
    exportTypes: List<ExportType>,
    measuring: List<Measuring>,
    companyName: String
  ) {
    exportDialog!!.closeConfirmDialog()
    export(exportTypes, measuring, companyName)
  }

  private fun export(
    exportTypes: List<ExportType>,
    measuring: List<Measuring>,
    companyName: String
  ) {
    ((context) as MainActivity).exporter.export(exportTypes, measuring, companyName) {
//        openExcelDocument(path)
      toast(R.string.you_measuring_exported_successfully)
    }
  }

  fun nothingToExport() {
    toast(R.string.nothingToExport)
  }

  fun noCheckedExportTypes() {
    exportDialog!!.closeConfirmDialog()
    toast(R.string.noCheckedExportTypes)
  }

  fun showExportDialog(measuring: List<Measuring>, companyName: String) {
    if (exportDialog == null) exportDialog = ExportDialog.getInstance(requireContext())
    exportDialog!!.openConfirmDialog(exportDialogListener, measuring, companyName)
  }
}