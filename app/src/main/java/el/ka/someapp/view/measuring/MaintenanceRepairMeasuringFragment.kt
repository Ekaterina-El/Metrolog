package el.ka.someapp.view.measuring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.measuring.DateType
import el.ka.someapp.data.model.measuring.MeasuringPart
import el.ka.someapp.data.model.role.AccessType
import el.ka.someapp.data.model.role.hasRole
import el.ka.someapp.databinding.FragmentMaintenanceRepairMeasuringBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.MaintenanceRepairViewModel
import el.ka.someapp.viewmodel.NodesViewModel
import java.util.*

class MaintenanceRepairMeasuringFragment : BaseFragment() {
  private lateinit var binding: FragmentMaintenanceRepairMeasuringBinding

  private val nodesViewModel: NodesViewModel by activityViewModels()
  private var maintenanceRepairViewModel: MaintenanceRepairViewModel? = null

  private var stateObserver = androidx.lifecycle.Observer<State> {
    if (it != State.LOADING) hideLoadingDialog()

    when (it) {
      State.LOADING -> showLoadingDialog()
      State.BACK -> goBack()
      else -> {}
    }
  }

  override fun initFunctionalityParts() {
    binding = FragmentMaintenanceRepairMeasuringBinding.inflate(layoutInflater)
    maintenanceRepairViewModel = ViewModelProvider(this)[MaintenanceRepairViewModel::class.java]
    maintenanceRepairViewModel!!.loadMeasuring(
      measuring = nodesViewModel.currentMeasuring.value!!,
      viewerRole = nodesViewModel.currentRole.value!!
    )
    updateAccessToEditFields()
  }

  override fun inflateBindingVariables() {
    binding.apply {
      master = this@MaintenanceRepairMeasuringFragment
      lifecycleOwner = viewLifecycleOwner
      viewModel = maintenanceRepairViewModel
    }
  }

  private fun updateAccessToEditFields() {
    val hasAccess =
      hasRole(maintenanceRepairViewModel!!.viewerRole.value!!, AccessType.EDIT_MEASURING)

    listOf<View>(
      binding.layoutPlace,
      binding.layoutLaboratory
    ).forEach { it.isEnabled = hasAccess }
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
    maintenanceRepairViewModel!!.state.observe(viewLifecycleOwner, stateObserver)

    val hasAccess =
      hasRole(maintenanceRepairViewModel!!.viewerRole.value!!, AccessType.EDIT_MEASURING)
    if (hasAccess) {
      // region Date Pickers Listeners
      binding.datePickerLast.setOnClickListener { showDatePicker(DateType.LAST) }
      binding.datePickerNext.setOnClickListener { showDatePicker(DateType.NEXT) }
      // endregion
    }
  }

  override fun onStop() {
    super.onStop()
    maintenanceRepairViewModel!!.state.removeObserver(stateObserver)

    binding.datePickerLast.setOnClickListener(null)
    binding.datePickerNext.setOnClickListener(null)
  }

  fun trySaveMeasuring() {
    maintenanceRepairViewModel!!.saveMeasuring {
      nodesViewModel.updateMeasuringPart(MeasuringPart.MAINTENANCE_REPAIR, it)
    }
  }

  private val datePickerListener = object : Companion.DatePickerListener {
    override fun onPick(date: Date) {
      maintenanceRepairViewModel!!.saveDate(date)
    }
  }

  // region Date Picker Dialog
  private fun showDatePicker(type: DateType) {
    maintenanceRepairViewModel!!.setEditTime(type)
    val date = when (type) {
      DateType.LAST -> maintenanceRepairViewModel!!.lastDate.value
      DateType.NEXT -> maintenanceRepairViewModel!!.nextDate.value
      else -> null
    }
    showDatePickerDialog(date, datePickerListener)
  }
  // endregion
}