package el.ka.someapp.view.node

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.R
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.LocalUser
import el.ka.someapp.data.model.Node
import el.ka.someapp.data.model.State
import el.ka.someapp.databinding.FragmentNodeInfoBinding
import el.ka.someapp.databinding.JobFieldDialogBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.view.adapters.HierarchyNodesAdapter
import el.ka.someapp.view.adapters.JobsAdapter
import el.ka.someapp.viewmodel.JobFieldViewModel
import el.ka.someapp.viewmodel.NodesViewModel
import el.ka.someapp.viewmodel.RegistrationViewModel

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
  private val localUsersListener = object : JobsAdapter.ItemListener {
    override fun onClick(userId: String) {}

  }

  private var changeNameDialog: Dialog? = null
  private var stateObserver = Observer<State> {
    when (it) {
      State.NON_UNIQUE_NAME ->
        showChangeNameDialogWithError(getString(Errors.nonUniqueName.textId))
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
    localUsersAdapter = JobsAdapter(listener = localUsersListener)
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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.state.observe(viewLifecycleOwner, stateObserver)
    viewModel.nodesHistory.observe(viewLifecycleOwner, hierarchyObserver)
    viewModel.localUser.observe(viewLifecycleOwner, localUsersObserver)
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.state.removeObserver(stateObserver)
    viewModel.nodesHistory.removeObserver(hierarchyObserver)
    viewModel.localUser.removeObserver(localUsersObserver)
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
    val editTextName = changeNameDialog!!.findViewById<EditText>(R.id.editTextNodeName)
    //editTextName.setText(viewModel.currentNode.value!!.name)

    changeNameDialog!!.findViewById<Button>(R.id.buttonOk).setOnClickListener {
      val value = editTextName.text.toString()
      viewModel.changeNodeName(value)
      clearDialog()
      changeNameDialog!!.dismiss()
    }
  }

  private fun clearDialog() {
    changeNameDialog!!.findViewById<EditText>(R.id.editTextNodeName).setText("")
    changeNameDialog!!.findViewById<TextView>(R.id.textError).text = ""
  }

  private fun showChangeNameDialogWithError(errorMessage: String) {
    changeNameDialog?.findViewById<TextView>(R.id.textError)?.text = errorMessage
    showChangeNameDialog()
  }

  fun showChangeNameDialog() {
    if (changeNameDialog == null) createChangeNameDialog()

    val editTextName = changeNameDialog!!.findViewById<EditText>(R.id.editTextNodeName)
    editTextName.setText(viewModel.currentNode.value!!.name)

    changeNameDialog?.show()
  }
  // endregion

  // region Job Field Dialog
  private var jobFieldDialog: Dialog? = null
  private lateinit var jobFieldViewModel: JobFieldViewModel

  private fun createJobFieldDialog() {
    jobFieldDialog = Dialog(requireContext())
    jobFieldViewModel  = ViewModelProvider(this)[JobFieldViewModel::class.java]

    jobFieldDialog?.let { dialog ->
      val binding: JobFieldDialogBinding = DataBindingUtil.inflate(
        LayoutInflater.from(requireContext()),
        R.layout.job_field_dialog,
        null,
        false
      )
      binding.viewModel = jobFieldViewModel

      dialog.setContentView(binding.root)
      dialog.window!!.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
      )
      dialog.setCancelable(true)
    }
  }

  fun showJobFieldDialog() {
    if (jobFieldDialog == null) createJobFieldDialog()
    jobFieldDialog!!.show()
  }
  // endregion
}