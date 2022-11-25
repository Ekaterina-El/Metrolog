package el.ka.someapp.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import el.ka.someapp.R

class ConfirmDialog(context: Context) : Dialog(context) {

  init {
    initDialog()
  }

  private fun initDialog() {
    setContentView(R.layout.confirm_dialog)
    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    window!!.setWindowAnimations(R.style.Slide)
    setCancelable(true)
  }

  fun openConfirmDialog(message: String, confirmListener: ConfirmListener, value: Any? = null) {
    findViewById<Button>(R.id.buttonYes).setOnClickListener { confirmListener.onAgree(value) }
    findViewById<Button>(R.id.buttonCancel).setOnClickListener { confirmListener.onDisagree() }
    findViewById<TextView>(R.id.textViewMessage).text = message
    show()
  }

  fun closeConfirmDialog() {
    findViewById<Button>(R.id.buttonYes).setOnClickListener(null)
    findViewById<Button>(R.id.buttonCancel).setOnClickListener(null)
    findViewById<TextView>(R.id.textViewMessage).text = ""
    dismiss()
  }

  companion object {
    interface ConfirmListener {
      fun onAgree(value: Any? = null)
      fun onDisagree()
    }

    private var dialog: ConfirmDialog? = null
    fun getInstance(context: Context): ConfirmDialog {
      dialog = if (dialog == null) ConfirmDialog(context) else dialog
      return dialog!!
    }
  }
}