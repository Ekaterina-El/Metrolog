package el.ka.someapp.view.measuring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.data.model.measuring.MeasuringPart
import el.ka.someapp.databinding.FragmentVerificationMeasuringBinding
import el.ka.someapp.viewmodel.MeasuringPartViewModel
import el.ka.someapp.viewmodel.VerificationMeasuringViewModel

class VerificationMeasuringFragment : MeasuringPartFragment() {
  private lateinit var binding: FragmentVerificationMeasuringBinding

  override val measuringPart = MeasuringPart.VERIFICATION
  override lateinit var viewModel: MeasuringPartViewModel

  override var controlInterface = listOf<View>()

  override fun initFunctionalityParts() {
    binding = FragmentVerificationMeasuringBinding.inflate(layoutInflater)
    controlInterface = listOf<View>(
      binding.layoutVerificationCodeCSM,
      binding.layoutCost,
      binding.layoutLaboratory
    )
    viewModel =
      ViewModelProvider(this)[VerificationMeasuringViewModel::class.java]

    val measuring = nodesViewModel.currentMeasuring.value!!
    viewModel.loadMeasuring(
      measuring,
      viewerRole = nodesViewModel.currentRole.value!!,
      measuring.verification
    )
    updateAccessToEditFields()
  }

  override fun inflateBindingVariables() {
    binding.apply {
      master = this@VerificationMeasuringFragment
      lifecycleOwner = viewLifecycleOwner
      viewModel = this@VerificationMeasuringFragment.viewModel as VerificationMeasuringViewModel
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

  override fun onBackPressed() {
    goBack()
  }

  private fun goBack() {
    popUp()
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