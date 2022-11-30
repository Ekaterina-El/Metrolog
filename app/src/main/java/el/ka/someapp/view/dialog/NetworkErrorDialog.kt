package el.ka.someapp.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.Button
import el.ka.someapp.R

class NetworkErrorDialog(context: Context) : Dialog(context) {
  init {
    initDialog()
  }

  private fun initDialog() {
    setContentView(R.layout.network_error_dialog)
    window!!.setLayout(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )
    window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    window!!.setWindowAnimations(R.style.Slide)
    setCancelable(true)

    val buttonOk: Button = findViewById(R.id.buttonOk)

    buttonOk.setOnClickListener {
      dismiss()
    }
  }

  fun showDialog() {
    show()
  }

  companion object {
    private var dialog: NetworkErrorDialog? = null
    fun getInstance(context: Context): NetworkErrorDialog {
      dialog = if (dialog == null) NetworkErrorDialog(context) else dialog
      return dialog!!
    }
  }
}