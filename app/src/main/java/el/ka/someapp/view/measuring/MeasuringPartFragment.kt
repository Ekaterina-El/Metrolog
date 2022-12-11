package el.ka.someapp.view.measuring

import android.view.View
import androidx.fragment.app.activityViewModels
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.measuring.DateType
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.data.model.measuring.MeasuringPart
import el.ka.someapp.data.model.role.AccessType
import el.ka.someapp.data.model.role.hasRole
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.MeasuringPartViewModel
import el.ka.someapp.viewmodel.NodesViewModel
import java.util.*

abstract class MeasuringPartFragment : BaseFragment() {
  abstract val measuringPart: MeasuringPart
  abstract val viewModel: MeasuringPartViewModel

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

  abstract var controlInterface: List<View>
  fun updateAccessToEditFields() {
    controlInterface.forEach { it.isEnabled = hasAccess }
  }

  override fun onBackPressed() {
    goBack()
  }

  private fun goBack() {
    popUp()
  }

  override fun onResume() {
    super.onResume()
    viewModel.state.observe(viewLifecycleOwner, stateObserver)
  }

  override fun onStop() {
    super.onStop()
    viewModel.state.removeObserver(stateObserver)
  }

  fun trySaveMeasuring() {
    viewModel.saveMeasuring {
      nodesViewModel.updateMeasuringPart(measuringPart, it)
    }
  }

  // region Date Picker Dialog
  private val datePickerListener = object : Companion.DatePickerListener {
    override fun onPick(date: Date) {
      viewModel.saveDate(date)
    }
  }

  fun showLastDatePicker() = showDatePicker(DateType.LAST)
  fun showNextDatePicker() = showDatePicker(DateType.NEXT)

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