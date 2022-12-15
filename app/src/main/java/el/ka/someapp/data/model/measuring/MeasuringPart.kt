package el.ka.someapp.data.model.measuring

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import el.ka.someapp.R
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.databinding.*
import el.ka.someapp.view.measuring.MeasuringPartFragment
import el.ka.someapp.viewmodel.*

enum class MeasuringPart(
  val dbTitle: String,
  val actionType: MeasuringActionType?,
  val strRes: Int
) {
  PASSPORT("passport", MeasuringActionType.EDITED_PASSPORT, R.string.passport),
  MAINTENANCE_REPAIR(
    "maintenanceRepair",
    MeasuringActionType.EDITED_MAINTENANCE_REPAIR,
    R.string.maintenance_repair
  ),
  TO("to", MeasuringActionType.EDITED_TO, R.string.to),
  OVERHAUL("overhaul", MeasuringActionType.EDITED_OVERHAUL, R.string.overhaul),
  VERIFICATION("verification", MeasuringActionType.EDITED_VERIFICATION, R.string.verification),
  CERTIFICATION("certification", MeasuringActionType.EDITED_CERTIFICATION, R.string.certification),
  CALIBRATION("calibration", MeasuringActionType.EDITED_CALIBRATION, R.string.calibration),
  HISTORY("history", null, 0)
}


data class MeasuringPartView(
  val binding: ViewDataBinding,
  val viewModel: MeasuringPartViewModel,
  val controlInterface: List<View>,
  val datePickerLast: View? = null,
  val datePickerNext: View? = null,
)

data class MeasuringPartArguments(
  val layoutInflater: LayoutInflater,
  val measuring: Measuring,
  val viewerRole: UserRole,
  val owner: ViewModelStoreOwner,
  val master: MeasuringPartFragment,
  val viewLifecycleOwner: LifecycleOwner?
)

fun MeasuringPart.getMeasuringPartView(
  args: MeasuringPartArguments
) = when (this) {
  MeasuringPart.OVERHAUL -> initOverhaulPart(args)
  MeasuringPart.CERTIFICATION -> initCertificationPart(args)
  MeasuringPart.VERIFICATION -> initVerificationPart(args)
  MeasuringPart.MAINTENANCE_REPAIR -> initMaintenanceRepairPart(args)
  MeasuringPart.TO -> initTOPart(args)
  MeasuringPart.PASSPORT -> initPassportPart(args)
  MeasuringPart.CALIBRATION -> initCalibrationPart(args)
  else -> null
}


// region Init functions for Measuring Parts
fun initCalibrationPart(args: MeasuringPartArguments): MeasuringPartView = with(args) {
  val binding = FragmentCalibrationMeasuringBinding.inflate(layoutInflater)
  val controlInterface = binding.controlInterface

  val viewModel = ViewModelProvider(owner)[CalibrationViewModel::class.java]
  viewModel.loadMeasuring(measuring, viewerRole, measuring.calibration)

  binding.master = master
  binding.lifecycleOwner = viewLifecycleOwner
  binding.viewModel = viewModel

  return MeasuringPartView(
    binding,
    viewModel,
    controlInterface,
    binding.datePickerLast,
    binding.datePickerNext
  )
}

private fun initPassportPart(args: MeasuringPartArguments): MeasuringPartView = with(args) {
  val binding = FragmentPassportMeasuringBinding.inflate(layoutInflater)
  val controlInterface = binding.controlInterface

  val viewModel = ViewModelProvider(owner)[PassportViewModel::class.java]
  viewModel.loadMeasuring(measuring, viewerRole, measuring.passport)

  binding.master = master
  binding.lifecycleOwner = viewLifecycleOwner
  binding.viewModel = viewModel

  return MeasuringPartView(binding, viewModel, controlInterface)
}

private fun initTOPart(args: MeasuringPartArguments): MeasuringPartView = with(args) {
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

private fun initVerificationPart(args: MeasuringPartArguments): MeasuringPartView = with(args) {
  val binding = FragmentVerificationMeasuringBinding.inflate(layoutInflater)
  val controlInterface = binding.controlInterface

  val viewModel = ViewModelProvider(owner)[VerificationMeasuringViewModel::class.java]
  viewModel.loadMeasuring(measuring, viewerRole, measuring.verification)

  binding.master = master
  binding.lifecycleOwner = viewLifecycleOwner
  binding.viewModel = viewModel

  return MeasuringPartView(
    binding, viewModel, controlInterface, binding.datePickerLast, binding.datePickerNext
  )
}

private fun initOverhaulPart(args: MeasuringPartArguments): MeasuringPartView = with(args) {
  val binding = FragmentOverhaulMeasuringBinding.inflate(layoutInflater)
  val controlInterface = binding.controlInterface

  val viewModel = ViewModelProvider(owner)[OverhaulViewModel::class.java]
  viewModel.loadMeasuring(measuring, viewerRole, measuring.overhaul)

  binding.master = master
  binding.lifecycleOwner = viewLifecycleOwner
  binding.viewModel = viewModel

  return MeasuringPartView(
    binding, viewModel, controlInterface, binding.datePickerLast, binding.datePickerNext
  )
}

private fun initCertificationPart(args: MeasuringPartArguments): MeasuringPartView = with(args) {
  val binding = FragmentCertificationMeasuringBinding.inflate(layoutInflater)
  val controlInterface = binding.controlInterface

  val viewModel = ViewModelProvider(owner)[CertificationMeasuringViewModel::class.java]
  viewModel.loadMeasuring(measuring, viewerRole, measuring.certification)

  binding.master = master
  binding.lifecycleOwner = viewLifecycleOwner
  binding.viewModel = viewModel

  return MeasuringPartView(
    binding, viewModel, controlInterface, binding.datePickerLast, binding.datePickerNext
  )
}


private fun initMaintenanceRepairPart(args: MeasuringPartArguments): MeasuringPartView =
  with(args) {
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
      binding, viewModel, controlInterface, binding.datePickerLast, binding.datePickerNext
    )
  }
// endregion

// region Control Interface list for every Measuring Part Binding
private val FragmentOverhaulMeasuringBinding.controlInterface: List<View>
  get() = listOf(layoutPlace, layoutLaboratory)

private val FragmentCertificationMeasuringBinding.controlInterface: List<View>
  get() = listOf(layoutLaboratory)

private val FragmentMaintenanceRepairMeasuringBinding.controlInterface: List<View>
  get() = listOf<View>(layoutPlace, layoutLaboratory)

private val FragmentToMeasuringBinding.controlInterface: List<View>
  get() = listOf()

private val FragmentCalibrationMeasuringBinding.controlInterface: List<View>
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
// endregion
