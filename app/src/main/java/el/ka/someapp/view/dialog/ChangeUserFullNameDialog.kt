package el.ka.someapp.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import el.ka.someapp.R
import el.ka.someapp.databinding.ChangeUserDialogBinding
import el.ka.someapp.viewmodel.ChangeUserFullNameViewModel

class ChangeUserFullNameDialog(
  context: Context,
  private val viewModel: ChangeUserFullNameViewModel,
  private val lifecycleObserver: LifecycleOwner
) : Dialog(context) {
  private lateinit var bindingChangeUserFullNameDialog: ChangeUserDialogBinding

  init {
    initDialog()
  }

  private fun initDialog() {
    bindingChangeUserFullNameDialog = ChangeUserDialogBinding.inflate(LayoutInflater.from(context))
    setContentView(bindingChangeUserFullNameDialog.root)

    window!!.setLayout(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )
    window!!.setWindowAnimations(R.style.Slide)

    setCancelable(true)

    bindingChangeUserFullNameDialog.viewModel = viewModel
    bindingChangeUserFullNameDialog.lifecycleOwner = lifecycleObserver
  }

  fun showDialog() {
    show()
  }

  fun clearDialog() {
    bindingChangeUserFullNameDialog.layoutFullName.error = null
  }

  fun dismissDialog() {
    clearDialog()
    dismiss()
  }

  fun showDialogWithError(error: String) {
    bindingChangeUserFullNameDialog.layoutFullName.error = error
  }

  companion object {
    private var dialog: ChangeUserFullNameDialog? = null
    fun getInstance(
      context: Context,
      viewModel: ChangeUserFullNameViewModel,
      lifecycleObserver: LifecycleOwner
    ): ChangeUserFullNameDialog {
      dialog = if (dialog == null) ChangeUserFullNameDialog(context, viewModel, lifecycleObserver) else dialog
      return dialog!!
    }
  }
}