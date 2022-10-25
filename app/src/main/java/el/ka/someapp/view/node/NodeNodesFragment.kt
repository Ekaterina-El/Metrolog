package el.ka.someapp.view.node

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import el.ka.someapp.data.model.Node
import el.ka.someapp.databinding.FragmentNodeInfoBinding
import el.ka.someapp.databinding.FragmentNodeNodesBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.view.adapters.NodesAdapter
import el.ka.someapp.viewmodel.NodesViewModel

class NodeNodesFragment : BaseFragment() {
  private lateinit var binding: FragmentNodeNodesBinding
  private val viewModel: NodesViewModel by activityViewModels()

  private lateinit var adapter: NodesAdapter
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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.filteredNodes.observe(viewLifecycleOwner, nodesObserver)
  }

  override fun initFunctionalityParts() {
    binding = FragmentNodeNodesBinding.inflate(layoutInflater)
    adapter = NodesAdapter()
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
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.filteredNodes.removeObserver(nodesObserver)
  }
}