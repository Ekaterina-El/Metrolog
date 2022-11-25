package el.ka.someapp.view.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import el.ka.someapp.R

class AddUserDialog(
  context: Context,
  private var listener: Listener? = null
) : Dialog(context) {


  init {
    initDialog()
  }

  private fun initDialog() {
    setContentView(R.layout.add_user_by_email_dialog)
    window!!.setLayout(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )
    window!!.setWindowAnimations(R.style.Slide)
    setCancelable(true)

    val buttonOk: Button = findViewById(R.id.buttonOk)

    buttonOk.setOnClickListener {
      val email = findViewById<EditText>(R.id.editTextUserEmail).text.toString()
      listener?.addUser(email)
//        viewModel.addUserToProjectByEmail(email = email)
//        dialog.dismiss()
    }
  }

  fun clearDialog() {
    findViewById<EditText>(R.id.editTextUserEmail).setText("")
    findViewById<TextInputLayout>(R.id.layoutEmail).error = null
  }

  fun show(error: String) {
    findViewById<TextInputLayout>(R.id.layoutEmail).error = error
    show()
  }

  fun setListener(listener: AddUserDialog.Companion.Listener) {
    this@AddUserDialog.listener = listener
  }

  companion object {
    interface Listener {
      fun addUser(email: String)
    }

    private var dialog: AddUserDialog? = null
    fun getInstance(context: Context, listener: Listener? = null): AddUserDialog {
      dialog = if (dialog == null) AddUserDialog(context, listener) else dialog
      return dialog!!
    }
  }
}