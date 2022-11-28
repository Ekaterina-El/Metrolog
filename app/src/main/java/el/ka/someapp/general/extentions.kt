package el.ka.someapp.general

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import java.text.SimpleDateFormat
import java.util.*

fun Date.convertDate(): String {
  val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
  return sdf.format(this)
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
