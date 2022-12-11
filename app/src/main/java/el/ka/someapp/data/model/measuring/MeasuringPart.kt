package el.ka.someapp.data.model.measuring

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.databinding.FragmentCertificationMeasuringBinding
import el.ka.someapp.databinding.FragmentOverhaulMeasuringBinding
import el.ka.someapp.databinding.FragmentVerificationMeasuringBinding
import el.ka.someapp.view.measuring.MeasuringPartFragment
import el.ka.someapp.viewmodel.CertificationMeasuringViewModel
import el.ka.someapp.viewmodel.MeasuringPartViewModel
import el.ka.someapp.viewmodel.OverhaulViewModel
import el.ka.someapp.viewmodel.VerificationMeasuringViewModel

enum class MeasuringPart(val dbTitle: String) {
  PASSPORT("passport"),
  MAINTENANCE_REPAIR("maintenanceRepair"),
  TO("to"),
  OVERHAUL("overhaul"),
  VERIFICATION("verification"),
  CERTIFICATION("certification")
}


fun MeasuringPart.getMeasuringPartView(
  measuring: Measuring,
  layoutInflater: LayoutInflater,
  viewerRole: UserRole,
  owner: ViewModelStoreOwner,
  master: MeasuringPartFragment,
  viewLifecycleOwner: LifecycleOwner?
): MeasuringPartView? {
  return when (this) {
    MeasuringPart.OVERHAUL -> initOverhaulPart(
      layoutInflater,
      measuring,
      viewerRole,
      owner,
      master,
      viewLifecycleOwner
    )
    MeasuringPart.CERTIFICATION -> initCertificationPart(
      layoutInflater,
      measuring,
      viewerRole,
      owner,
      master,
      viewLifecycleOwner
    )
    MeasuringPart.VERIFICATION -> initVerificationPart(
      layoutInflater,
      measuring,
      viewerRole,
      owner,
      master,
      viewLifecycleOwner
    )
    else -> null
  }
}

fun initVerificationPart(
  layoutInflater: LayoutInflater,
  measuring: Measuring,
  viewerRole: UserRole,
  owner: ViewModelStoreOwner,
  master: MeasuringPartFragment,
  viewLifecycleOwner: LifecycleOwner?
): MeasuringPartView {
  val binding = FragmentVerificationMeasuringBinding.inflate(layoutInflater)
  val controlInterface = binding.controlInterface

  val viewModel = ViewModelProvider(owner)[VerificationMeasuringViewModel::class.java]
  viewModel.loadMeasuring(measuring, viewerRole, measuring.verification)

  binding.master = master
  binding.lifecycleOwner = viewLifecycleOwner
  binding.viewModel = viewModel

  return MeasuringPartView(
    binding, viewModel, controlInterface, binding.datePickerLast,
    binding.datePickerNext
  )
}

fun initOverhaulPart(
  layoutInflater: LayoutInflater,
  measuring: Measuring,
  viewerRole: UserRole,
  owner: ViewModelStoreOwner,
  master: MeasuringPartFragment,
  viewLifecycleOwner: LifecycleOwner?
): MeasuringPartView {
  val binding = FragmentOverhaulMeasuringBinding.inflate(layoutInflater)
  val controlInterface = binding.controlInterface

  val viewModel = ViewModelProvider(owner)[OverhaulViewModel::class.java]
  viewModel.loadMeasuring(measuring, viewerRole, measuring.overhaul)

  binding.master = master
  binding.lifecycleOwner = viewLifecycleOwner
  binding.viewModel = viewModel

  return MeasuringPartView(
    binding, viewModel, controlInterface, binding.datePickerLast,
    binding.datePickerNext
  )
}

fun initCertificationPart(
  layoutInflater: LayoutInflater,
  measuring: Measuring,
  viewerRole: UserRole,
  owner: ViewModelStoreOwner,
  master: MeasuringPartFragment,
  viewLifecycleOwner: LifecycleOwner?
): MeasuringPartView {
  val binding = FragmentCertificationMeasuringBinding.inflate(layoutInflater)
  val controlInterface = binding.controlInterface

  val viewModel = ViewModelProvider(owner)[CertificationMeasuringViewModel::class.java]
  viewModel.loadMeasuring(measuring, viewerRole, measuring.certification)

  binding.master = master
  binding.lifecycleOwner = viewLifecycleOwner
  binding.viewModel = viewModel

  return MeasuringPartView(
    binding,
    viewModel,
    controlInterface, binding.datePickerLast, binding.datePickerNext
  )
}


private val FragmentOverhaulMeasuringBinding.controlInterface: List<View>
  get() = listOf(layoutPlace, layoutLaboratory)

private val FragmentCertificationMeasuringBinding.controlInterface: List<View>
  get() = listOf(layoutLaboratory)

private val FragmentVerificationMeasuringBinding.controlInterface: List<View>
  get() = listOf(
    layoutVerificationCodeCSM,
    layoutCost,
    layoutLaboratory
  )

data class MeasuringPartView(
  val binding: ViewDataBinding,
  val viewModel: MeasuringPartViewModel,
  val controlInterface: List<View>,
  val datePickerLast: View? = null,
  val datePickerNext: View? = null,
)
