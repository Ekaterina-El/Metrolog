package el.ka.someapp.view.measuring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.measuring.*
import el.ka.someapp.data.model.role.AccessType
import el.ka.someapp.data.model.role.hasRole
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.MeasuringPartViewModel
import el.ka.someapp.viewmodel.NodesViewModel
import java.util.*

open class MeasuringPartFragment(val measuringPart: MeasuringPart) :
  BaseFragment() {
  lateinit var viewModel: MeasuringPartViewModel

  val measuring: Measuring
    get() = nodesViewModel.currentMeasuring.value!!

  val viewerRole: UserRole
    get() = nodesViewModel.currentRole.value!!

  val nodesViewModel: NodesViewModel by activityViewModels()

  val hasAccess: Boolean
    get() = hasRole(viewModel.viewerRole.value!!, AccessType.EDIT_MEASURING)

  var stateObserver = androidx.lifecycle.Observer<State> {
    if (it != State.LOADING) hideLoadingDialog()
    when (it) {
      State.LOADING -> showLoadingDialog()
      State.BACK -> goBack()
      else -> {}
    }
  }

  private var controlInterface: List<View> = listOf()
  open fun updateAccessToEditFields() {
    controlInterface.forEach { it.isEnabled = hasAccess }
  }

  lateinit var viewDateBinding: ViewDataBinding

  override fun initFunctionalityParts() {
    val parts = measuringPart.getMeasuringPartView(
      MeasuringPartArguments(
        layoutInflater,
        measuring,
        viewerRole,
        this,
        this,
        viewLifecycleOwner
      )
    )

    viewDateBinding = parts.binding
    viewModel = parts.viewModel
    controlInterface = parts.controlInterface
    datePickerLast = parts.datePickerLast
    datePickerNext = parts.datePickerNext

    updateAccessToEditFields()
  }

  override fun inflateBindingVariables() {}

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return viewDateBinding.root
  }

  override fun onBackPressed() {
    goBack()
  }

  private fun goBack() {
    popUp()
  }

  private var datePickerLast: View? = null
  private var datePickerNext: View? = null

  override fun onResume() {
    super.onResume()
    viewModel.state.observe(viewLifecycleOwner, stateObserver)

    if (hasAccess) {
      datePickerLast?.setOnClickListener { showLastDatePicker() }
      datePickerNext?.setOnClickListener { showNextDatePicker() }
    }
  }

  override fun onStop() {
    super.onStop()
    viewModel.state.removeObserver(stateObserver)
    datePickerLast?.setOnClickListener(null)
    datePickerNext?.setOnClickListener(null)
  }

  open fun allFieldsRight(): Boolean = true

  fun trySaveMeasuring() {
    if (allFieldsRight()) viewModel.saveMeasuring {
      nodesViewModel.updateMeasuringPart(measuringPart, it)
    }
  }

  // region Date Picker Dialog
  private val datePickerListener = object : Companion.DatePickerListener {
    override fun onPick(date: Date) {
      viewModel.saveDate(date)
    }
  }

  private fun showLastDatePicker() = showDatePicker(DateType.LAST)
  private fun showNextDatePicker() = showDatePicker(DateType.NEXT)

  fun showDatePicker(type: DateType) {
    viewModel.setEditTime(type)
    val date = when (type) {
      DateType.LAST -> viewModel.lastDate.value
      DateType.NEXT -> viewModel.nextDate.value
      else -> null
    }
    showDatePickerDialog(date, datePickerListener)
  }
  // endregion
}