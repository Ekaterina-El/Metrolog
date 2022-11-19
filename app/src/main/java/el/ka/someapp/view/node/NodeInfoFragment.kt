package el.ka.someapp.view.node

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import el.ka.someapp.R
import el.ka.someapp.data.model.*
import el.ka.someapp.databinding.FragmentNodeInfoBinding
import el.ka.someapp.databinding.JobFieldDialogBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.view.adapters.HierarchyNodesAdapter
import el.ka.someapp.view.adapters.JobsAdapter
import el.ka.someapp.view.adapters.SpinnerUsersAdapter
import el.ka.someapp.viewmodel.JobFieldViewModel
import el.ka.someapp.viewmodel.NodesViewModel

class NodeInfoFragment : BaseFragment() {
  private lateinit var binding: FragmentNodeInfoBinding
  private val viewModel: NodesViewModel by activityViewModels()

  private lateinit var hierarchyAdapter: HierarchyNodesAdapter
  private val hierarchyObserver = Observer<List<Node>> {
    hierarchyAdapter.setNodes(it)
  }
  private val hierarchyNodeListener = object : HierarchyNodesAdapter.ItemListener {
    override fun onClick(node: Node) {
      viewModel.navigateByHistoryTo(node)
    }
  }

  private lateinit var localUsersAdapter: JobsAdapter
  private val localUsersObserver = Observer<List<LocalUser>> {
    localUsersAdapter.setLocalUser(it)
  }

  private val  currentNodeObserver = Observer<Node?> {
    if (it != null) viewModel.updateLocalUsers()
  }

  private val localUsersListener = object : JobsAdapter.ItemListener {
    override fun onClick(jobField: JobField) {}
    override fun onEdit(jobField: JobField) {
      viewModel.setToEditJobField(jobField)
    }

    override fun onDelete(jobField: JobField) {
      viewModel.deleteJobField(jobField)
    }

  }

  private var changeNameDialog: Dialog? = null
  private var stateObserver = Observer<State> {
    when (it) {
      State.NON_UNIQUE_NAME ->
        showChangeNameDialogWithError(getString(Errors.nonUniqueName.textId))

      State.EDIT_JOB_FIELD ->
        openJobFieldDialogToEdit(viewModel.editJobField.value!!)
      else -> {}
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun initFunctionalityParts() {
    binding = FragmentNodeInfoBinding.inflate(layoutInflater)
    hierarchyAdapter = HierarchyNodesAdapter(listener = hierarchyNodeListener)
    localUsersAdapter = JobsAdapter(context = requireContext(), listener = localUsersListener)
  }

  override fun inflateBindingVariables() {
    binding.apply {
      viewmodel = this@NodeInfoFragment.viewModel
      master = this@NodeInfoFragment
      lifecycleOwner = viewLifecycleOwner
      hierarchyAdapter = this@NodeInfoFragment.hierarchyAdapter
      localUsersAdapter = this@NodeInfoFragment.localUsersAdapter
    }
  }


  override fun onBackPressed() {
    viewModel.goBack()
  }


  override fun onResume() {
    super.onResume()
    viewModel.state.observe(viewLifecycleOwner, stateObserver)
    viewModel.nodesHistory.observe(viewLifecycleOwner, hierarchyObserver)
    viewModel.localUser.observe(viewLifecycleOwner, localUsersObserver)
    jobFieldViewModel?.state?.observe(viewLifecycleOwner, jobFieldStateObserver)
    viewModel.currentNode.observe(viewLifecycleOwner, currentNodeObserver)
  }

  override fun onStop() {
    super.onStop()
    viewModel.state.removeObserver(stateObserver)
    viewModel.nodesHistory.removeObserver(hierarchyObserver)
    viewModel.localUser.removeObserver(localUsersObserver)
    jobFieldViewModel?.state?.removeObserver { jobFieldStateObserver }
  }

  // region Change Node Name Dialog
  private fun createChangeNameDialog() {
    changeNameDialog = Dialog(requireActivity())
    changeNameDialog!!.setContentView(R.layout.add_node_dialog)
    changeNameDialog!!.window!!.setLayout(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )
    changeNameDialog!!.setCancelable(true)
    changeNameDialog!!.findViewById<TextView>(R.id.textTitle).text = getString(R.string.edit)
    val editTextName = changeNameDialog!!.findViewById<EditText>(R.id.inp1)

    changeNameDialog!!.findViewById<Button>(R.id.buttonOk).setOnClickListener {
      val value = editTextName.text.toString()
      viewModel.changeNodeName(value)
      clearDialog()
      changeNameDialog!!.dismiss()
    }
  }

  private fun clearDialog() {
    changeNameDialog!!.findViewById<EditText>(R.id.inp1).setText("")
    changeNameDialog!!.findViewById<TextInputLayout>(R.id.layoutName).error = null
  }

  private fun showChangeNameDialogWithError(errorMessage: String) {
    changeNameDialog?.findViewById<TextInputLayout>(R.id.layoutName)?.error = errorMessage
    showChangeNameDialog()
  }

  fun showChangeNameDialog() {
    if (changeNameDialog == null) createChangeNameDialog()

    val editTextName = changeNameDialog!!.findViewById<EditText>(R.id.inp1)
    editTextName.setText(viewModel.currentNode.value!!.name)

    changeNameDialog?.show()
  }
  // endregion

  // region Job Field Dialog
  private var jobFieldDialog: Dialog? = null
  private var jobFieldViewModel: JobFieldViewModel? = null
  private lateinit var bindingJobFieldDialog: JobFieldDialogBinding
  private var spinnerUsersAdapter: SpinnerUsersAdapter? = null
  private var checked: RadioButton? = null

  private val jobFieldStateObserver = Observer<State> {
    when (it) {
      State.NEW_FIELD_JOB_ADDED -> {
        jobFieldViewModel!!.afterNotifiedOfNewFieldJob()
        jobFieldDialog?.dismiss()
        val jobField = jobFieldViewModel!!.jobField.value!!
        viewModel.addJobField(jobField)
      }

      State.JOB_FIELD_EDITED -> {
        val jobField = jobFieldViewModel!!.jobField.value!!
        val oldJobField = jobFieldViewModel!!.oldJobField.value!!

        jobFieldViewModel!!.afterNotifiedOfNewFieldJob()
        jobFieldDialog?.dismiss()
        viewModel.updateJobField(oldJobField, jobField)
      }
      else -> {}
    }
  }

  private fun createJobFieldDialog() {
    jobFieldDialog = Dialog(requireContext())
    if (spinnerUsersAdapter == null)
      spinnerUsersAdapter = SpinnerUsersAdapter(requireContext(), viewModel.companyAllUsers.value!!)

    jobFieldViewModel = ViewModelProvider(this)[JobFieldViewModel::class.java]
    jobFieldViewModel?.state?.observe(viewLifecycleOwner, jobFieldStateObserver)

    jobFieldDialog?.let { dialog ->
      bindingJobFieldDialog = JobFieldDialogBinding.inflate(LayoutInflater.from(requireContext()))
      dialog.setContentView(bindingJobFieldDialog.root)

      dialog.setOnDismissListener {
        clearJobFieldDialog()
      }

      bindingJobFieldDialog.spinner.adapter = spinnerUsersAdapter
      bindingJobFieldDialog.spinner.onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
          override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            val user = p0!!.selectedItem as User
            jobFieldViewModel!!.setUser(user)
          }

          override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
      bindingJobFieldDialog.viewModel = jobFieldViewModel
      bindingJobFieldDialog.lifecycleOwner = viewLifecycleOwner

      dialog.window!!.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
      )
      dialog.setCancelable(true)

      bindingJobFieldDialog.buttonOk.setOnClickListener {
        val nodeId = viewModel.currentNode.value!!.id

        if (jobFieldViewModel!!.state.value == State.EDIT_JOB_FIELD)
          jobFieldViewModel!!.editJobField(nodeId)
        else jobFieldViewModel!!.tryCreateJobField(nodeId)
      }
    }
  }

  fun showJobFieldDialog() {
    if (jobFieldDialog == null) createJobFieldDialog() else updateUsers()

    // Установка роли по умолчанию "Читатель"
    bindingJobFieldDialog.rbReader.isChecked = true
    jobFieldViewModel!!.role.value = UserRole.READER

    jobFieldDialog!!.show()
  }

  private fun updateUsers() {
    spinnerUsersAdapter = SpinnerUsersAdapter(requireContext(), viewModel.companyAllUsers.value!!)
    bindingJobFieldDialog.spinner.adapter = spinnerUsersAdapter
  }

  private fun clearJobFieldDialog() {
    bindingJobFieldDialog.textTitle.text = getString(R.string.add_job_field)
    jobFieldViewModel!!.clearFields()

    val elementsState = View.VISIBLE
    bindingJobFieldDialog.fieldJobName.visibility = elementsState
    bindingJobFieldDialog.layoutJobFieldRole.visibility = elementsState
    bindingJobFieldDialog.textViewRole.visibility = elementsState

//    checked?.isChecked = false
  }

  private fun openJobFieldDialogToEdit(jobField: JobField) {
    if (jobFieldDialog == null) createJobFieldDialog()

    bindingJobFieldDialog.textTitle.text = getString(R.string.edit_job_field)

    val user = viewModel.getUserById(jobField.userId)
    jobFieldViewModel!!.setJobField(jobField, user)

    val pos = spinnerUsersAdapter!!.getPosition(user)
    bindingJobFieldDialog.spinner.setSelection(pos)

    val elementsState = if (jobField.jobRole == UserRole.HEAD) View.GONE else View.VISIBLE
    bindingJobFieldDialog.fieldJobName.visibility = elementsState
    bindingJobFieldDialog.layoutJobFieldRole.visibility = elementsState
    bindingJobFieldDialog.textViewRole.visibility = elementsState

    checked = when (jobField.jobRole) {
      UserRole.READER -> bindingJobFieldDialog.rbReader
      UserRole.EDITOR_1 -> bindingJobFieldDialog.editor1
      UserRole.EDITOR_2 -> bindingJobFieldDialog.editor2
      else -> null
    }
    checked?.isChecked = true

    jobFieldDialog!!.show()
  }
  // endregion
}