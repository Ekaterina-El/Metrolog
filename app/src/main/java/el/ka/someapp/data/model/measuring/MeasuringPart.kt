package el.ka.someapp.data.model.measuring

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.databinding.*
import el.ka.someapp.view.measuring.MeasuringPartFragment
import el.ka.someapp.viewmodel.*

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

    MeasuringPart.MAINTENANCE_REPAIR -> initMaintenanceRepairPart(
      layoutInflater,
      measuring,
      viewerRole,
      owner,
      master,
      viewLifecycleOwner
    )

    MeasuringPart.TO -> initTOPart(
      layoutInflater,
      measuring,
      viewerRole,
      owner,
      master,
      viewLifecycleOwner
    )

    MeasuringPart.PASSPORT -> initPassportPart(
      layoutInflater,
      measuring,
      viewerRole,
      owner,
      master,
      viewLifecycleOwner
    )
  }
}

fun initPassportPart(
  layoutInflater: LayoutInflater,
  measuring: Measuring,
  viewerRole: UserRole,
  owner: ViewModelStoreOwner,
  master: MeasuringPartFragment,
  viewLifecycleOwner: LifecycleOwner?
): MeasuringPartView {
  val binding = FragmentPassportMeasuringBinding.inflate(layoutInflater)
  val controlInterface = binding.controlInterface

  val viewModel = ViewModelProvider(owner)[PassportViewModel::class.java]
  viewModel.loadMeasuring(measuring, viewerRole, measuring.passport)

  binding.master = master
  binding.lifecycleOwner = viewLifecycleOwner
  binding.viewModel = viewModel

  return MeasuringPartView(
    binding, viewModel, controlInterface
  )
}

fun initTOPart(
  layoutInflater: LayoutInflater,
  measuring: Measuring,
  viewerRole: UserRole,
  owner: ViewModelStoreOwner,
  master: MeasuringPartFragment,
  viewLifecycleOwner: LifecycleOwner?
): MeasuringPartView {
  val binding = FragmentToMeasuringBinding.inflate(layoutInflater)
  val controlInterface = binding.controlInterface

  val viewModel = ViewModelProvider(owner)[TOViewModel::class.java]
  viewModel.loadMeasuring(measuring, viewerRole, measuring.TO)

  binding.master = master
  binding.lifecycleOwner = viewLifecycleOwner
  binding.viewModel = viewModel

  return MeasuringPartView(
    binding, viewModel, controlInterface, binding.datePickerLast, binding.datePickerNext
  )
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


fun initMaintenanceRepairPart(
  layoutInflater: LayoutInflater,
  measuring: Measuring,
  viewerRole: UserRole,
  owner: ViewModelStoreOwner,
  master: MeasuringPartFragment,
  viewLifecycleOwner: LifecycleOwner?
): MeasuringPartView {
  val binding = FragmentMaintenanceRepairMeasuringBinding.inflate(layoutInflater)
  val controlInterface = binding.controlInterface

  val viewModel = ViewModelProvider(owner)[MaintenanceRepairViewModel::class.java]
  viewModel.loadMeasuring(
    measuring,
    viewerRole,
    measuring.maintenanceRepair
  )

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

private val FragmentMaintenanceRepairMeasuringBinding.controlInterface: List<View>
  get() = listOf<View>(layoutPlace, layoutLaboratory)

private val FragmentToMeasuringBinding.controlInterface: List<View>
  get() = listOf()

private val FragmentPassportMeasuringBinding.controlInterface: List<View>
  get() = listOf<View>(
    layoutName,
    spinnerMeasurementKind,
    spinnerMeasurementCategory,
    layoutMeasuringType,
    layoutInventoryName,
    layoutSerialName,
    layoutRegNameGRSI,
    layoutManufacturer,
    layoutSupplier,
    layoutSectorGROEI,
    spinnerMeasurementType,
    spinnerMeasurementStatus,
    spinnerMeasurementCondition,
  )


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
