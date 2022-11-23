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
import el.ka.someapp.databinding.FragmentVerificationMeasuringBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.NodesViewModel
import el.ka.someapp.viewmodel.VerificationMeasuringViewModel
import java.util.*

class VerificationMeasuringFragment : BaseFragment() {
  private lateinit var binding: FragmentVerificationMeasuringBinding

  private val nodesViewModel: NodesViewModel by activityViewModels()
  private var verificationMeasuringViewModel: VerificationMeasuringViewModel? = null

  private var stateObserver = androidx.lifecycle.Observer<State> {
    if (it != State.LOADING) hideLoadingDialog()
    when (it) {
      State.LOADING -> showLoadingDialog()
      State.BACK -> goBack()
      else -> {}
    }
  }

  override fun initFunctionalityParts() {
    binding = FragmentVerificationMeasuringBinding.inflate(layoutInflater)
    verificationMeasuringViewModel = ViewModelProvider(this)[VerificationMeasuringViewModel::class.java]
    verificationMeasuringViewModel!!.loadMeasuring(
      measuring = nodesViewModel.currentMeasuring.value!!,
      viewerRole = nodesViewModel.currentRole.value!!
    )
    updateAccessToEditFields()
  }

  override fun inflateBindingVariables() {
    binding.apply {
      master = this@VerificationMeasuringFragment
      lifecycleOwner = viewLifecycleOwner
      viewModel = verificationMeasuringViewModel
    }
  }

  private fun updateAccessToEditFields() {
    val hasAccess =
      hasRole(verificationMeasuringViewModel!!.viewerRole.value!!, AccessType.EDIT_MEASURING)

    listOf<View>(
      binding.layoutVerificationCodeCSM,
      binding.layoutCost,
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
    verificationMeasuringViewModel!!.state.observe(viewLifecycleOwner, stateObserver)

    val hasAccess =
      hasRole(verificationMeasuringViewModel!!.viewerRole.value!!, AccessType.EDIT_MEASURING)
    if (hasAccess) {
      // region Date Pickers Listeners
      binding.datePickerLast.setOnClickListener { showDatePicker(DateType.LAST) }
      binding.datePickerNext.setOnClickListener { showDatePicker(DateType.NEXT) }
      // endregion
    }
  }

  override fun onStop() {
    super.onStop()
    verificationMeasuringViewModel!!.state.removeObserver(stateObserver)

    binding.datePickerLast.setOnClickListener(null)
    binding.datePickerNext.setOnClickListener(null)
  }

  fun trySaveMeasuring() {
    verificationMeasuringViewModel!!.saveMeasuring {
      nodesViewModel.updateMeasuringPart(MeasuringPart.VERIFICATION, it)
    }
  }

  private val datePickerListener = object : Companion.DatePickerListener {
    override fun onPick(date: Date) {
      verificationMeasuringViewModel!!.saveDate(date)
    }
  }

  // region Date Picker Dialog
  private fun showDatePicker(type: DateType) {
    verificationMeasuringViewModel!!.setEditTime(type)
    val date = when (type) {
      DateType.LAST -> verificationMeasuringViewModel!!.lastDate.value
      DateType.NEXT -> verificationMeasuringViewModel!!.nextDate.value
      else -> null
    }
    showDatePickerDialog(date, datePickerListener)
  }
  // endregion
}