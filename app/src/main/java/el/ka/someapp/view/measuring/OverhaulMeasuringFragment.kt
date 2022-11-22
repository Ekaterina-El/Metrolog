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
import el.ka.someapp.databinding.FragmentOverhaulMeasuringBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.NodesViewModel
import el.ka.someapp.viewmodel.OverhaulViewModel
import el.ka.someapp.viewmodel.PassportViewModel
import java.util.*

class OverhaulMeasuringFragment : BaseFragment() {
  private lateinit var binding: FragmentOverhaulMeasuringBinding

  private val nodesViewModel: NodesViewModel by activityViewModels()
  private var overhaulViewModel: OverhaulViewModel? = null

  private var stateObserver = androidx.lifecycle.Observer<State> {
    if (it != State.LOADING) hideLoadingDialog()

    when (it) {
      State.LOADING -> showLoadingDialog()
      State.BACK -> goBack()
      else -> {}
    }
  }

  override fun initFunctionalityParts() {
    binding = FragmentOverhaulMeasuringBinding.inflate(layoutInflater)
    overhaulViewModel = ViewModelProvider(this)[OverhaulViewModel::class.java]
    overhaulViewModel!!.loadMeasuring(
      measuring = nodesViewModel.currentMeasuring.value!!,
      viewerRole = nodesViewModel.currentRole.value!!
    )
    updateAccessToEditFields()
  }

  override fun inflateBindingVariables() {
    binding.apply {
      master = this@OverhaulMeasuringFragment
      lifecycleOwner = viewLifecycleOwner
      viewModel = overhaulViewModel
    }
  }

  private fun updateAccessToEditFields() {
    val hasAccess = hasRole(overhaulViewModel!!.viewerRole.value!!, AccessType.EDIT_MEASURING)

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
    val hasAccess = hasRole(overhaulViewModel!!.viewerRole.value!!, AccessType.EDIT_MEASURING)

    super.onResume()
    overhaulViewModel!!.state.observe(viewLifecycleOwner, stateObserver)

    if (hasAccess) {
      // region Date Pickers Listeners
      binding.datePickerLast.setOnClickListener { showDatePicker(DateType.LAST) }
      binding.datePickerNext.setOnClickListener { showDatePicker(DateType.NEXT) }
      // endregion
    }
  }

  override fun onStop() {
    super.onStop()
    overhaulViewModel!!.state.removeObserver(stateObserver)

    binding.datePickerLast.setOnClickListener(null)
    binding.datePickerNext.setOnClickListener(null)
  }

  fun trySaveMeasuring() {
    overhaulViewModel!!.saveMeasuring {
      nodesViewModel.updateMeasuringPart(MeasuringPart.OVERHAUL, it)
    }
  }

  private val datePickerListener = object : Companion.DatePickerListener {
    override fun onPick(date: Date) {
      overhaulViewModel!!.saveDate(date)
    }
  }

  // region Date Picker Dialog
  private fun showDatePicker(type: DateType) {
    overhaulViewModel!!.setEditTime(type)
    val date = when (type) {
      DateType.LAST -> overhaulViewModel!!.lastDate.value
      DateType.NEXT -> overhaulViewModel!!.nextDate.value
      else -> null
    }
    showDatePickerDialog(date, datePickerListener)
  }
  // endregion
}