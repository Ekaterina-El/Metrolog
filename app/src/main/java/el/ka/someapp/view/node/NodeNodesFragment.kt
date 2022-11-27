package el.ka.someapp.view.node

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.Node
import el.ka.someapp.data.model.State
import el.ka.someapp.databinding.FragmentNodeNodesBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.view.adapters.NodesAdapter
import el.ka.someapp.view.dialog.AddCompanyDialog
import el.ka.someapp.viewmodel.NodesViewModel

class NodeNodesFragment : BaseFragment() {
  private lateinit var binding: FragmentNodeNodesBinding
  private val viewModel: NodesViewModel by activityViewModels()

  private lateinit var adapter: NodesAdapter
  private val nodesAdapterListener = object: NodesAdapter.ItemListener {
    override fun onClick(nodeId: String) {
      viewModel.loadNodeByID(nodeId)
    }

  }
  private val nodesObserver = Observer<List<Node>> {
    adapter.setNodes(it)
  }

  private val stateObserver = Observer<State> {
    when (it) {
      State.NON_UNIQUE_NAME -> {
        showCreatedDialogWithError(getString(Errors.nonUniqueName.textId))
        viewModel.toViewState()
      }
      State.NEW_NODE_ADDED -> {
        addNodeDialog!!.dismiss()
        viewModel.toViewState()
      }
      else -> {}
    }
  }

  private val currentNodeObserver = Observer<Node?> {
    viewModel.loadNodes()
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
    binding = FragmentNodeNodesBinding.inflate(layoutInflater)

    val d = DividerItemDecoration(binding.listCompanies.context, DividerItemDecoration.VERTICAL)
    binding.listCompanies.addItemDecoration(d)
    adapter = NodesAdapter(nodesAdapterListener)
  }

  override fun inflateBindingVariables() {
    binding.apply {
      viewmodel = this@NodeNodesFragment.viewModel
      master = this@NodeNodesFragment
      lifecycleOwner = viewLifecycleOwner
      adapter = this@NodeNodesFragment.adapter
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.filteredNodes.observe(viewLifecycleOwner, nodesObserver)
    viewModel.state.observe(viewLifecycleOwner, stateObserver)
    viewModel.currentNode.observe(viewLifecycleOwner, currentNodeObserver)
  }

  override fun onStop() {
    super.onStop()
    viewModel.filteredNodes.removeObserver(nodesObserver)
    viewModel.state.removeObserver(stateObserver)
    viewModel.currentNode.removeObserver(currentNodeObserver)
  }

  override fun onBackPressed() {
    viewModel.goBack()
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.filteredNodes.removeObserver(nodesObserver)
    viewModel.state.removeObserver(stateObserver)
    viewModel.currentNode.removeObserver(currentNodeObserver)
  }

  // region Dialog Add Node
  private var addNodeDialog: AddCompanyDialog? = null
  private val addNodeDialogListener = object: AddCompanyDialog.Companion.Listener {
    override fun saveNode(value: String) {
      if (value.isNotEmpty()) viewModel.addNodeWithName(value) else addNodeDialog!!.clearDialog()
      addNodeDialog!!.dismiss()
    }
  }

  fun showAddNodeDialog() {
    if (addNodeDialog == null) addNodeDialog =  AddCompanyDialog(requireContext())
    addNodeDialog!!.setListener(addNodeDialogListener)
    addNodeDialog!!.showDialog()
  }

  private fun showCreatedDialogWithError(error: String) {
    if (addNodeDialog == null) addNodeDialog =  AddCompanyDialog(requireContext())
    addNodeDialog!!.setListener(addNodeDialogListener)
    addNodeDialog!!.showWithError(error, currentName = "")
  }
  // endregion
}