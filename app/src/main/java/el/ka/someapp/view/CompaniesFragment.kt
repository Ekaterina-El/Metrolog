package el.ka.someapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
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
  }

  override fun inflateBindingVariables() {
    binding.listCompanies.itemAnimator = null

    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      adapter = this@CompaniesFragment.adapter
      viewModel = this@CompaniesFragment.viewModel
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.loadMainNodes()
    viewModel.filteredNodes.observe(viewLifecycleOwner, nodesObserver)
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.filteredNodes.removeObserver { nodesObserver }
  }

  override fun onBackPressed() {}
}