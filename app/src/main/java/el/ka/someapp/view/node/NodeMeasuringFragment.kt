package el.ka.someapp.view.node

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import el.ka.someapp.R
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.databinding.FragmentNodeMeasuringBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.view.adapters.MeasuringAdapter
import el.ka.someapp.viewmodel.NodesViewModel
import el.ka.someapp.viewmodel.VisibleViewModel

class NodeMeasuringFragment: BaseFragment() {
  private lateinit var binding: FragmentNodeMeasuringBinding
  private val viewModel: NodesViewModel by activityViewModels()
  private val visibleViewModel: VisibleViewModel by activityViewModels()

  private lateinit var measuringAdapter: MeasuringAdapter

  private val measuringObserver = Observer<List<Measuring>> {
    measuringAdapter.setMeasuring(it)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel.loadMeasuring()
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
    binding = FragmentNodeMeasuringBinding.inflate(layoutInflater)

    measuringAdapter = MeasuringAdapter()
    val d = DividerItemDecoration(binding.listMeasuring.context, DividerItemDecoration.VERTICAL)
    binding.listMeasuring.addItemDecoration(d)
  }

  override fun inflateBindingVariables() {
    binding.apply {
      viewmodel = this@NodeMeasuringFragment.viewModel
      master = this@NodeMeasuringFragment
      lifecycleOwner = viewLifecycleOwner
      measuringAdapter = this@NodeMeasuringFragment.measuringAdapter
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.loadMeasuring()
    viewModel.measuringFiltered.observe(viewLifecycleOwner, measuringObserver)
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.measuringFiltered.removeObserver(measuringObserver)
  }

  override fun onBackPressed() {
    viewModel.goBack()
  }

  fun goAddMeasuring() {
    visibleViewModel.setNodeNavigationState(false)
    navigate(R.id.action_nodeMeasuringFragment_to_addMeasuringFragment)
  }
}