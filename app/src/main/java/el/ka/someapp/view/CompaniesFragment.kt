package el.ka.someapp.view

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import el.ka.someapp.R
import el.ka.someapp.data.model.Node
import el.ka.someapp.databinding.FragmentCompaniesBinding
import el.ka.someapp.view.adapters.NodesAdapter
import el.ka.someapp.viewmodel.NodesViewModel


class CompaniesFragment : BaseFragment() {
  private lateinit var binding: FragmentCompaniesBinding
  private lateinit var adapter: NodesAdapter
  private val viewModel: NodesViewModel by activityViewModels()
  private val nodesObserver = Observer<List<Node>> {
    adapter.setNodes(it)
  }

  private lateinit var dialog: Dialog


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun initFunctionalityParts() {
    binding = FragmentCompaniesBinding.inflate(layoutInflater)
    adapter = NodesAdapter()
    createAddCompanyDialog()
  }

  override fun inflateBindingVariables() {
    binding.listCompanies.itemAnimator = null

    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      adapter = this@CompaniesFragment.adapter
      viewModel = this@CompaniesFragment.viewModel
      master = this@CompaniesFragment
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.loadMainNodes()
    viewModel.filteredNodes.observe(viewLifecycleOwner, nodesObserver)
  }

  private fun createAddCompanyDialog() {
    dialog = Dialog(requireActivity())
    dialog.setContentView(R.layout.add_node_dialog)
    dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    dialog.setCancelable(true)

    val editTextNodeName: EditText = dialog.findViewById(R.id.editTextNodeName)
    val buttonOk: Button = dialog.findViewById(R.id.buttonOk)

    buttonOk.setOnClickListener {
      viewModel.addNodeWithName(editTextNodeName.text.toString())
      dialog.dismiss()
    }
  }

  fun showAddCompanyDialog() {
    dialog.show()
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.filteredNodes.removeObserver { nodesObserver }
  }

  override fun onBackPressed() {}
}