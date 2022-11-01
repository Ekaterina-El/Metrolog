package el.ka.someapp.view.node

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import el.ka.someapp.R
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.Node
import el.ka.someapp.data.model.State
import el.ka.someapp.databinding.FragmentNodeNodesBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.view.adapters.NodesAdapter
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
      }
      State.NEW_NODE_ADDED -> {
        clearDialog()
        viewModel.toViewState()
      }
      else -> {}
    }
  }
  private lateinit var dialog: Dialog

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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.filteredNodes.observe(viewLifecycleOwner, nodesObserver)
    viewModel.state.observe(viewLifecycleOwner, stateObserver)
    viewModel.currentNode.observe(viewLifecycleOwner, currentNodeObserver)
  }

  override fun initFunctionalityParts() {
    binding = FragmentNodeNodesBinding.inflate(layoutInflater)
    adapter = NodesAdapter(nodesAdapterListener)
    createAddNodeDialog()
  }

  override fun inflateBindingVariables() {
    binding.apply {
      viewmodel = this@NodeNodesFragment.viewModel
      master = this@NodeNodesFragment
      lifecycleOwner = viewLifecycleOwner
      adapter = this@NodeNodesFragment.adapter
    }
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
  private fun createAddNodeDialog() {
    dialog = Dialog(requireActivity())
    dialog.setContentView(R.layout.add_node_dialog)
    dialog.window!!.setLayout(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )
    dialog.setCancelable(true)

    val editTextNodeName: EditText = dialog.findViewById(R.id.editTextNodeName)
    val buttonOk: Button = dialog.findViewById(R.id.buttonOk)

    buttonOk.setOnClickListener {
      val value = editTextNodeName.text.toString()
      if (value.isNotEmpty()) viewModel.addNodeWithName(value) else clearDialog()
      dialog.dismiss()
    }
  }

  private fun showCreatedDialogWithError(error: String) {
    dialog.findViewById<TextView>(R.id.textError).text = error
    showAddNodeDialog()
  }

  fun showAddNodeDialog() {
    dialog.show()
  }

  private fun clearDialog() {
    dialog.findViewById<EditText>(R.id.editTextNodeName).setText("")
    dialog.findViewById<TextView>(R.id.textError).text = null
  }
  // endregion
}