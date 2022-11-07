package el.ka.someapp.view.node

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import el.ka.someapp.databinding.FragmentAddMeasuringBinding
import el.ka.someapp.view.BaseFragment

class AddMeasuringFragment : BaseFragment() {
  private lateinit var binding: FragmentAddMeasuringBinding
//  private val viewModel: NodesViewModel by activityViewModels()


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun initFunctionalityParts() {
    binding = FragmentAddMeasuringBinding.inflate(layoutInflater)
  }

  override fun inflateBindingVariables() {
    binding.apply {
//      viewmodel = this@AddMeasuringFragment.viewModel
      lifecycleOwner = viewLifecycleOwner
      master = this@AddMeasuringFragment
    }
  }

  override fun onBackPressed() {
//    viewModel.goBack()
  }
}