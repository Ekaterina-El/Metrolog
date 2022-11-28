package el.ka.someapp.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.LifecycleOwner
import el.ka.someapp.R
import el.ka.someapp.data.model.SpinnerItem
import el.ka.someapp.data.model.User
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.databinding.JobFieldDialogBinding
import el.ka.someapp.general.addListener
import el.ka.someapp.view.adapters.SpinnerUsersAdapter
import el.ka.someapp.viewmodel.JobFieldViewModel

class JobFieldDialog(
  context: Context,
  private val allUsers: List<User>,
  private var listener: Listener,
  private var viewModel: JobFieldViewModel,
  private val lifecycleOwner: LifecycleOwner,
  private var spinnerUsersAdapter: SpinnerUsersAdapter? = null,
  private val bindingJobFieldDialog: JobFieldDialogBinding
) : Dialog(context) {
  init {
    initDialog()
  }

  private fun initDialog() {
    if (spinnerUsersAdapter == null)
      spinnerUsersAdapter = SpinnerUsersAdapter(context, allUsers)

    setContentView(bindingJobFieldDialog.root)

    setOnDismissListener { clearJobFieldDialog() }

    bindingJobFieldDialog.spinnerRole.addListener {
      val role = (it as SpinnerItem).value as UserRole
      listener.selectRole(role)
//        jobFieldViewModel!!.setRole(role)
    }

    bindingJobFieldDialog.spinner.adapter = spinnerUsersAdapter
    bindingJobFieldDialog.spinner.onItemSelectedListener =
      object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
          val user = p0!!.selectedItem as User
          listener.selectUser(user)
//            jobFieldViewModel!!.setUser(user)
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {}
      }
    bindingJobFieldDialog.viewModel = viewModel
    bindingJobFieldDialog.lifecycleOwner = lifecycleOwner

    window!!.setLayout(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )
    window!!.setWindowAnimations(R.style.Slide)
    setCancelable(true)


    bindingJobFieldDialog.buttonOk.setOnClickListener { listener.onSave() }
  }

  fun showJobFieldDialog(users: List<User>) {
    updateUsers(users)
    viewModel.role.value = UserRole.READER
    show()
  }

  private fun updateUsers(users: List<User>) {
    spinnerUsersAdapter = SpinnerUsersAdapter(context, users)
    bindingJobFieldDialog.spinner.adapter = spinnerUsersAdapter
  }

  private fun clearJobFieldDialog() {
    bindingJobFieldDialog.textTitle.text = context.getString(R.string.add_job_field)
    viewModel.clearFields()

    val elementsState = View.VISIBLE
    bindingJobFieldDialog.fieldJobName.visibility = elementsState
    bindingJobFieldDialog.layoutJobFieldRole.visibility = elementsState
    bindingJobFieldDialog.textViewRole.visibility = elementsState
  }

  fun openJobFieldDialogToEdit(userPos: Int, jobRole: UserRole) {
    bindingJobFieldDialog.textTitle.text = context.getString(R.string.edit_job_field)

    bindingJobFieldDialog.spinner.setSelection(userPos)

    val elementsState = if (jobRole == UserRole.HEAD) View.GONE else View.VISIBLE
    bindingJobFieldDialog.fieldJobName.visibility = elementsState
    bindingJobFieldDialog.layoutJobFieldRole.visibility = elementsState
    bindingJobFieldDialog.textViewRole.visibility = elementsState

    show()
  }


  companion object {
    interface Listener {
      fun selectRole(userRole: UserRole)
      fun selectUser(user: User)
      fun onSave()
    }

    private var dialog: JobFieldDialog? = null
    fun getInstance(
      context: Context,
      allUsers: List<User>,
      listener: Listener,
      viewModel: JobFieldViewModel,
      lifecycleOwner: LifecycleOwner,
      spinnerUsersAdapter: SpinnerUsersAdapter? = null,
      bindingJobFieldDialog: JobFieldDialogBinding
    ): JobFieldDialog {
      dialog = if (dialog == null) JobFieldDialog(
        context,
        allUsers,
        listener,
        viewModel,
        lifecycleOwner,
        spinnerUsersAdapter,
        bindingJobFieldDialog
      ) else dialog
      return dialog!!
    }
  }
}