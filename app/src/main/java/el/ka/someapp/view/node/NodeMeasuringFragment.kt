package el.ka.someapp.view.node

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import el.ka.someapp.R
import el.ka.someapp.databinding.FragmentNodeMeasuringBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.NodesViewModel

class NodeMeasuringFragment: BaseFragment() {
  private lateinit var binding: FragmentNodeMeasuringBinding
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
    binding = FragmentNodeMeasuringBinding.inflate(layoutInflater)
  }

  override fun inflateBindingVariables() {
    binding.apply {
      viewmodel = this@NodeMeasuringFragment.viewModel
      master = this@NodeMeasuringFragment
      lifecycleOwner = viewLifecycleOwner
    }
  }

  override fun onBackPressed() {
    viewModel.goBack()
  }

  fun goAddMeasuring() {
    navigate(R.id.action_nodeMeasuringFragment_to_addMeasuringFragment)
  }
}