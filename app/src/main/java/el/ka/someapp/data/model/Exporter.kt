package el.ka.someapp.data.model

import android.os.Environment
import el.ka.someapp.data.model.measuring.Measuring
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class Exporter {
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
    const val SHEET_NAME = "TOP players"
    val documentsDir: String get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path
  }

  private fun createWorkbook(): Workbook {
    val workbook = XSSFWorkbook()
    val cellStyle = createCellStyle(workbook)
    val sheet: Sheet = workbook.createSheet(SHEET_NAME)
    addData(sheet, list, cellStyle)
    return workbook
  }

  private fun createCellStyle(workbook: XSSFWorkbook): XSSFCellStyle {
    val cellStyle: XSSFCellStyle = workbook.createCellStyle()
    cellStyle.setBorderTop(BorderStyle.MEDIUM)
    cellStyle.setBorderRight(BorderStyle.MEDIUM)
    cellStyle.setBorderBottom(BorderStyle.MEDIUM)
    cellStyle.setBorderLeft(BorderStyle.MEDIUM)

    return cellStyle
  }

  private fun addData(sheet: Sheet, items: List<Item>, cellStyle: XSSFCellStyle) {
    items.forEachIndexed { idx, item ->
      val row = sheet.createRow(idx)
      createCell(row, 0, item.name, cellStyle)
      createCell(row, 1, item.value, cellStyle)
    }
  }

  val list = listOf(
    Item("Mike", "470"),
    Item("Montessori", "460"),
    Item("Sandra", "300"),
    Item("Moringa", "300"),
    Item("Torres", "270"),
    Item("McGee", "420"),
    Item("Gibbs", "510"),
  )

  data class Item(val name: String, val value: String)

  private fun createCell(row: Row, columnIndex: Int, value: String?, cellStyle: XSSFCellStyle) {
    val cell = row.createCell(columnIndex)
    cell?.setCellValue(value)
    cell?.cellStyle = cellStyle
  }

  fun save(): String {
    return saveWorkbook(createWorkbook())
  }

  private fun saveWorkbook(workbook: Workbook): String {
    val filePath =
      File(
        documentsDir,
        "/Demo.xlsx"
      )
    if (!filePath.exists()) {
      filePath.createNewFile()
    }
    val outputStream = FileOutputStream(filePath)
    workbook.write(outputStream)

    outputStream.flush()
    outputStream.close()
    return filePath.path
  }

  fun export(
    exportTypes: List<ExportType>,
    measuring: List<Measuring>,
    onSuccess: (String) -> Unit
  ) {
//    Log.d("Export", "exportTypes: ${exportTypes.joinToString(", ")}")
    onSuccess("")
  }
}