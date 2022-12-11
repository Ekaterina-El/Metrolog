package el.ka.someapp.view.measuring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.data.model.measuring.MeasuringPart
import el.ka.someapp.databinding.FragmentCertificationMeasuringBinding
import el.ka.someapp.viewmodel.CertificationMeasuringViewModel
import el.ka.someapp.viewmodel.MeasuringPartViewModel


class CertificationMeasuringFragment : MeasuringPartFragment() {
  override val measuringPart: MeasuringPart
    get() = MeasuringPart.CERTIFICATION

  private lateinit var binding: FragmentCertificationMeasuringBinding
  override lateinit var viewModel: MeasuringPartViewModel

  private lateinit var controlViews: List<View>
  override var controlInterface: List<View>
    get() = controlViews
    set(value) {}

  override fun initFunctionalityParts() {
    binding = FragmentCertificationMeasuringBinding.inflate(layoutInflater)
    controlViews = listOf<View>(
      binding.layoutLaboratory
    )

    viewModel =
      ViewModelProvider(this)[CertificationMeasuringViewModel::class.java]

    val measuring = nodesViewModel.currentMeasuring.value!!
    viewModel.loadMeasuring(
      measuring,
      viewerRole = nodesViewModel.currentRole.value!!,
      measuring.certification
    )
    updateAccessToEditFields()
  }

  override fun inflateBindingVariables() {
    binding.apply {
      master = this@CertificationMeasuringFragment
      lifecycleOwner = viewLifecycleOwner
      viewModel = this@CertificationMeasuringFragment.viewModel as CertificationMeasuringViewModel
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