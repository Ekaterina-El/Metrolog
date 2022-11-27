package el.ka.someapp.view.node

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.R
import el.ka.someapp.data.model.*
import el.ka.someapp.data.model.measuring.Fields
import el.ka.someapp.databinding.FragmentNodeInfoBinding
import el.ka.someapp.databinding.JobFieldDialogBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.view.adapters.HierarchyNodesAdapter
import el.ka.someapp.view.adapters.JobsAdapter
import el.ka.someapp.view.adapters.SpinnerUsersAdapter
import el.ka.someapp.view.dialog.AddCompanyDialog
import el.ka.someapp.view.dialog.ConfirmDialog
import el.ka.someapp.view.dialog.JobFieldDialog
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

  private val roleObserver = Observer<UserRole?> {
    localUsersAdapter.updateRole(it)
  }


  private lateinit var localUsersAdapter: JobsAdapter
  private val localUsersObserver = Observer<List<LocalUser>> {
    localUsersAdapter.setLocalUser(it, viewModel.currentRole.value)
  }

  private val currentNodeObserver = Observer<Node?> {
    if (it != null) viewModel.updateLocalUsers()
  }

  private val localUsersListener = object : JobsAdapter.ItemListener {
    override fun onClick(jobField: JobField) {}
    override fun onEdit(jobField: JobField) {
      viewModel.setToEditJobField(jobField)

    }

    override fun onDelete(jobField: JobField) {
      showDeleteJobFieldConfirm(jobField)
    }
  }

  private var stateObserver = Observer<State> {
    when (it) {
      State.NON_UNIQUE_NAME ->
        showChangeNameDialogWithError(getString(Errors.nonUniqueName.textId))

      State.EDIT_JOB_FIELD -> {
        openJobFieldDialogToEdit(viewModel.editJobField.value!!)
        viewModel.toViewState()
      }
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
    viewModel.currentNode.observe(viewLifecycleOwner, currentNodeObserver)
    viewModel.currentRole.observe(viewLifecycleOwner, roleObserver)
  }

  override fun onStop() {
    super.onStop()
    viewModel.state.removeObserver(stateObserver)
    viewModel.nodesHistory.removeObserver(hierarchyObserver)
    viewModel.localUser.removeObserver(localUsersObserver)
    viewModel.currentRole.removeObserver(roleObserver)
  }

  // region Delete Job Field
  private val deleteJobFieldConfirmListener = object : ConfirmDialog.Companion.ConfirmListener {
    override fun onAgree(value: Any?) {
      viewModel.deleteJobField(value as JobField)
      closeConfirmDialog()
    }

    override fun onDisagree() {
      closeConfirmDialog()
    }
  }

  private fun showDeleteJobFieldConfirm(jobField: JobField) {
    openConfirmDialog(
      getString(R.string.delete_job_field_message),
      deleteJobFieldConfirmListener,
      jobField
    )
  }
  // endregion

  // region Delete node
  fun deleteNode() {
    showDeleteNodeConfirm()
  }

  private val deleteNodeConfirmListener = object : ConfirmDialog.Companion.ConfirmListener {
    override fun onAgree(value: Any?) {
      closeConfirmDialog()
      viewModel.deleteNode()
    }

    override fun onDisagree() {
      closeConfirmDialog()
    }
  }

  private fun showDeleteNodeConfirm() {
    openConfirmDialog(
      getString(R.string.delete_node),
      deleteNodeConfirmListener
    )
  }
  // endregion

  // region Change Node Name Dialog
  private var nodeNameCompanyDialog: AddCompanyDialog? = null
  private val nodeNameCompanyDialogListener = object : AddCompanyDialog.Companion.Listener {
    override fun saveNode(value: String) {
      viewModel.changeNodeName(value)
      nodeNameCompanyDialog!!.clearDialog()
      nodeNameCompanyDialog!!.dismiss()
    }
  }

  fun showChangeNameDialog() {
    if (nodeNameCompanyDialog == null) nodeNameCompanyDialog =
      AddCompanyDialog.getInstance(requireContext(), nodeNameCompanyDialogListener)
    nodeNameCompanyDialog!!.setListener(nodeNameCompanyDialogListener)
    nodeNameCompanyDialog!!.showDialog(currentName = viewModel.currentNode.value!!.name)
  }

  private fun showChangeNameDialogWithError(errorMessage: String) {
    nodeNameCompanyDialog!!.showWithError(
      errorMessage,
      currentName = viewModel.currentNode.value!!.name
    )
  }
  // endregion

  // region Job Field Dialog
  private lateinit var bindingJobFieldDialog: JobFieldDialogBinding
  private var spinnerUsersAdapter: SpinnerUsersAdapter? = null
  private var jobFieldDialog: JobFieldDialog? = null
  private lateinit var jobFieldViewModel: JobFieldViewModel

  private val jobFieldStateObserver = Observer<State> {
    when (it) {
      State.NEW_FIELD_JOB_ADDED -> {
        jobFieldViewModel.afterNotifiedOfNewFieldJob()
        jobFieldDialog?.dismiss()
        val jobField = jobFieldViewModel.jobField.value!!
        viewModel.addJobField(jobField)
      }

      State.JOB_FIELD_EDITED -> {
        val jobField = jobFieldViewModel.jobField.value!!
        val oldJobField = jobFieldViewModel.oldJobField.value!!
        jobFieldViewModel.afterNotifiedOfNewFieldJob()
        jobFieldDialog?.dismiss()
        viewModel.updateJobField(oldJobField, jobField)
      }
      else -> {}
    }
  }

  private val jobFieldDialogListener = object : JobFieldDialog.Companion.Listener {
    override fun selectRole(userRole: UserRole) {
      jobFieldViewModel.setRole(userRole)
    }

    override fun selectUser(user: User) {
      jobFieldViewModel.setUser(user)
    }

    override fun onSave() {
      val nodeId = viewModel.currentNode.value!!.id


      if (jobFieldViewModel.state.value == State.EDIT_JOB_FIELD)
        jobFieldViewModel.editJobField(nodeId)
      else jobFieldViewModel.tryCreateJobField(nodeId)
    }

  }

  private fun createJobFieldDialog() {
    jobFieldViewModel = ViewModelProvider(this)[JobFieldViewModel::class.java]
    jobFieldViewModel.state.observe(viewLifecycleOwner, jobFieldStateObserver)
    spinnerUsersAdapter = SpinnerUsersAdapter(requireContext(), viewModel.companyAllUsers.value!!)
    bindingJobFieldDialog = JobFieldDialogBinding.inflate(LayoutInflater.from(requireContext()))


    jobFieldDialog = JobFieldDialog.getInstance(
      requireContext(),
      viewModel.companyAllUsers.value!!,
      jobFieldDialogListener,
      jobFieldViewModel,
      viewLifecycleOwner,
      spinnerUsersAdapter,
      bindingJobFieldDialog
    )
  }

  fun showJobFieldDialog() {
    if (jobFieldDialog == null) createJobFieldDialog()

    createSpinner(
      R.array.rolesTypes, Fields.rolesTypeVariables,
      bindingJobFieldDialog.spinnerRole, UserRole.READER
    )
    jobFieldDialog!!.showJobFieldDialog(viewModel.companyAllUsers.value!!)
  }

  private fun openJobFieldDialogToEdit(jobField: JobField) {
    if (jobFieldDialog == null) createJobFieldDialog()

    val user = viewModel.getUserById(jobField.userId)
    jobFieldViewModel.setJobField(jobField, user)

    val pos = spinnerUsersAdapter!!.getPosition(user)

    createSpinner(
      R.array.rolesTypes, Fields.rolesTypeVariables,
      bindingJobFieldDialog.spinnerRole, jobField.jobRole
    )

    jobFieldDialog!!.openJobFieldDialogToEdit(pos, jobField.jobRole)
  }
  // endregion
}