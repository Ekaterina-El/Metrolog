package el.ka.someapp.view.measuring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import el.ka.someapp.databinding.FragmentMeasuringDashboardBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.NodesViewModel
import el.ka.someapp.viewmodel.VisibleViewModel

class MeasuringDashboardFragment : BaseFragment() {
  private lateinit var binding: FragmentMeasuringDashboardBinding

  private val nodesViewModel: NodesViewModel by activityViewModels()
  private val visibleViewModel: VisibleViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun initFunctionalityParts() {
    binding = FragmentMeasuringDashboardBinding.inflate(layoutInflater)
  }

  override fun inflateBindingVariables() {
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      viewModel = this@MeasuringDashboardFragment.nodesViewModel
      master = this@MeasuringDashboardFragment
    }
  }

  override fun onBackPressed() {
    visibleViewModel.setNodeNavigationState(true)
    nodesViewModel.setCurrentMeasuring(null)
    popUp()
  }
}