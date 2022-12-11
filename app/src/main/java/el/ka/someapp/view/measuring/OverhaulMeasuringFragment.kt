package el.ka.someapp.view.measuring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.data.model.measuring.MeasuringPart
import el.ka.someapp.databinding.FragmentOverhaulMeasuringBinding
import el.ka.someapp.viewmodel.MeasuringPartViewModel
import el.ka.someapp.viewmodel.OverhaulViewModel

class OverhaulMeasuringFragment : MeasuringPartFragment() {
  private lateinit var binding: FragmentOverhaulMeasuringBinding

  override val measuringPart = MeasuringPart.OVERHAUL
  override lateinit var viewModel: MeasuringPartViewModel

  override var controlInterface: List<View> = listOf()
  override fun initFunctionalityParts() {
    binding = FragmentOverhaulMeasuringBinding.inflate(layoutInflater)
    controlInterface = listOf<View>(binding.layoutPlace, binding.layoutLaboratory)

    viewModel = ViewModelProvider(this)[OverhaulViewModel::class.java]
    viewModel.loadMeasuring(measuring, viewerRole, measuring.overhaul)
    updateAccessToEditFields()
  }

  override fun inflateBindingVariables() {
    binding.apply {
      master = this@OverhaulMeasuringFragment
      lifecycleOwner = viewLifecycleOwner
      viewModel = this@OverhaulMeasuringFragment.viewModel as OverhaulViewModel
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun onResume() {
    super.onResume()
    if (hasAccess) {
      binding.datePickerLast.setOnClickListener { showLastDatePicker() }
      binding.datePickerNext.setOnClickListener { showNextDatePicker() }
    }
  }

  override fun onStop() {
    super.onStop()
    binding.datePickerLast.setOnClickListener(null)
    binding.datePickerNext.setOnClickListener(null)
  }
}