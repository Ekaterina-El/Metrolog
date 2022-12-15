package el.ka.someapp.data.model

import android.content.Context
import android.os.Environment
import el.ka.someapp.R
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.general.getCurrentTime
import el.ka.someapp.general.toRowsForExport
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class Exporter(val context: Context) {
  init {
    System.setProperty(
      "org.apache.poi.javax.xml.stream.XMLInputFactory",
      "com.fasterxml.aalto.stax.InputFactoryImpl"
    )
    System.setProperty(
      "org.apache.poi.javax.xml.stream.XMLOutputFactory",
      "com.fasterxml.aalto.stax.OutputFactoryImpl"
    )
    System.setProperty(
      "org.apache.poi.javax.xml.stream.XMLEventFactory",
      "com.fasterxml.aalto.stax.EventFactoryImpl"
    )
  }

  companion object {
    const val emptyCellsTop = 3
    const val emptyCellsLeft = 1
    val headersSize = listOf(35, 10, 25, 25, 25, 25, 25, 25)
    val documentsDir: String get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path
  }

  private val fileName: String get() = "Metrolog_${getCurrentTime().time}.xlsx"


  private fun createCellStyle(workbook: XSSFWorkbook): XSSFCellStyle {
    val cellStyle: XSSFCellStyle = workbook.createCellStyle()
    cellStyle.setBorderTop(BorderStyle.MEDIUM)
    cellStyle.setBorderRight(BorderStyle.MEDIUM)
    cellStyle.setBorderBottom(BorderStyle.MEDIUM)
    cellStyle.setBorderLeft(BorderStyle.MEDIUM)
    cellStyle.setVerticalAlignment(VerticalAlignment.TOP)

    cellStyle.wrapText = true
    return cellStyle
  }

  private fun createHeaderCellStyle(cellStyle: XSSFCellStyle): XSSFCellStyle {
    val headerStyle = cellStyle.clone() as XSSFCellStyle
    headerStyle.setAlignment(HorizontalAlignment.CENTER)
    headerStyle.setVerticalAlignment(VerticalAlignment.CENTER)

    return headerStyle
  }

  private fun setDate(
    sheet: Sheet,
    values: List<List<String>>
  ) {
    values.forEachIndexed { idx, rowValues -> createRow(sheet, idx, rowValues) }
  }

  private fun createRow(sheet: Sheet, idx: Int, rowValues: List<String>) {
    val row = sheet.createRow(idx + emptyCellsTop)
    val isHeader = (idx == 0)
    rowValues.forEachIndexed { idxValue, value ->
      val colIdx = idxValue + emptyCellsLeft
      createCell(row, colIdx, value, isHeader)
      if (isHeader) sheet.setColumnWidth(colIdx, headersSize[idxValue] * 256)
    }
  }

  private fun createCell(row: Row, columnIndex: Int, value: String?, isHeader: Boolean) {
    val cell = row.createCell(columnIndex)
    val v = if (value == null || value == "") "-" else value
    cell?.setCellValue(v)
    cell?.cellStyle = if (isHeader) headerStyle else cellStyle
  }

  private fun saveWorkbook(workbook: Workbook): String {
    val filePath = File(documentsDir, "/$fileName")
    if (!filePath.exists()) filePath.createNewFile()

    val outputStream = FileOutputStream(filePath)
    workbook.write(outputStream)
    outputStream.flush()
    outputStream.close()

    return filePath.path
  }

  private lateinit var cellStyle: XSSFCellStyle
  private lateinit var headerStyle: XSSFCellStyle

  fun export(
    exportTypes: List<ExportType>,
    measuring: List<Measuring>,
    companyName: String,
    onSuccess: (String) -> Unit
  ) {
    val workbook = XSSFWorkbook()
    createStyles(workbook)
    exportTypes.forEach { createSheet(workbook, measuring, companyName, it) }
    val path = saveWorkbook(workbook)
    onSuccess(path)
  }

  private fun createStyles(workbook: XSSFWorkbook) {
    cellStyle = createCellStyle(workbook)
    headerStyle = createHeaderCellStyle(cellStyle)
  }

  private val generalHeaders by lazy {
    listOf(
      context.getString(R.string.export_naming),
      context.getString(R.string.export_type),
      context.getString(R.string.export_serial_number),
      context.getString(R.string.export_inv_number),
      context.getString(R.string.export_location),
    )
  }

  private val passportHeader by lazy {
    listOf(
      context.getString(R.string.export_status),
      context.getString(R.string.export_condition),
    )
  }

  private val maintenanceRepairHeader by lazy {
    listOf(
      context.getString(R.string.lastDateRepair),
      context.getString(R.string.nextDateRepair),
      context.getString(R.string.place),
    )
  }

  private val overhaulHeader by lazy {
    listOf(
      context.getString(R.string.lastDateRepair),
      context.getString(R.string.nextDateRepair),
      context.getString(R.string.place),
    )
  }

  private val mTOHeader by lazy {
    listOf(
      context.getString(R.string.lastDateTO),
      context.getString(R.string.nextDateTO),
    )
  }

  private val verificationHeader by lazy {
    listOf(
      context.getString(R.string.lastDateVerification),
      context.getString(R.string.nextDateVerification),
    )
  }

  private val certificationHeader by lazy {
    listOf(
      context.getString(R.string.lastDateCertification),
      context.getString(R.string.nextDateCertification),
    )
  }

  private val calibrationHeader by lazy {
    listOf(
      context.getString(R.string.lastDateCalibration),
      context.getString(R.string.nextDateCalibration),
    )
  }

  private fun createSheet(
    workbook: XSSFWorkbook,
    measuring: List<Measuring>,
    companyName: String,
    exportType: ExportType
  ) {
    // Create sheet
    val title = context.getString(exportType.toMeasuringPart().strRes)
    val sheet = workbook.createSheet(title)

    // Create headers
    val headerItems = generalHeaders.toMutableList()
    headerItems.addAll(getHeaderItems(exportType))

    // Create values
    val values = mutableListOf(headerItems.toList())
    values.addAll(measuring.toRowsForExport(context, companyName, exportType))

    // Set values to sheet
    setDate(sheet, values)
  }

  private fun getHeaderItems(exportType: ExportType): List<String> = when (exportType) {
    ExportType.GENERAL -> passportHeader
    ExportType.TO -> mTOHeader
    ExportType.VERIFICATION -> verificationHeader
    ExportType.CALIBRATION -> calibrationHeader
    ExportType.CERTIFICATION -> certificationHeader
    ExportType.OVERHAUL -> overhaulHeader
    ExportType.MAINTENANCE_REPAIR -> maintenanceRepairHeader
  }


}