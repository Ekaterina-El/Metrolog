package el.ka.someapp.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import el.ka.someapp.R

class AddCompanyDialog(context: Context, val listener: Listener? = null): Dialog(context) {

  init {
    initDialog()
  }

  private fun initDialog() {
    setContentView(R.layout.add_node_dialog)
    window!!.setLayout(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )
    window!!.setWindowAnimations(R.style.Slide)
    setCancelable(true)

    val editTextNodeName: EditText = findViewById(R.id.inp1)
    val buttonOk: Button = findViewById(R.id.buttonOk)

    buttonOk.setOnClickListener {
      val value = editTextNodeName.text.toString()
      if (value.isNotEmpty()) listener?.saveNode(value) else clearDialog()
      dismiss()
    }
  }

  fun clearDialog() {
    findViewById<EditText>(R.id.inp1).setText("")
    findViewById<TextInputLayout>(R.id.layoutName).error = null
  }

  fun showWithError(error: String) {
    findViewById<TextInputLayout>(R.id.layoutName).error = error
    showDialog()
  }

  fun showDialog() {
    show()
  }

  companion object {
    interface Listener {
      fun saveNode(value: String)
    }

    private var dialog: AddCompanyDialog? = null
    fun getInstance(context: Context, listener: Listener? = null): AddCompanyDialog {
      dialog = if (dialog == null) AddCompanyDialog(context, listener) else dialog
      return dialog!!
    }
  }
}