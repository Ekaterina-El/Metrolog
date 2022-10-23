package el.ka.someapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import el.ka.someapp.R
import el.ka.someapp.data.model.Node
import el.ka.someapp.databinding.FragmentNodeBinding
import el.ka.someapp.viewmodel.NodesViewModel

class NodeFragment : BaseFragment() {
  private lateinit var binding: FragmentNodeBinding
  private val viewModel: NodesViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun initFunctionalityParts() {
    binding = FragmentNodeBinding.inflate(layoutInflater)
  }

  override fun inflateBindingVariables() {
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@NodeFragment
      viewmodel = this@NodeFragment.viewModel
    }
  }

  override fun onBackPressed() {
    navigate(R.id.action_nodeFragment_to_companiesFragment)
  }
}