package el.ka.someapp.view.node

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import el.ka.someapp.databinding.FragmentNodeInfoBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.NodesViewModel

class NodeInfoFragment : BaseFragment() {
  private lateinit var binding: FragmentNodeInfoBinding
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
    binding = FragmentNodeInfoBinding.inflate(layoutInflater)
  }

  override fun inflateBindingVariables() {
    binding.apply {
      viewmodel = this@NodeInfoFragment.viewModel
      master = this@NodeInfoFragment
      lifecycleOwner = viewLifecycleOwner
    }
  }

  override fun onBackPressed() {
  }
}