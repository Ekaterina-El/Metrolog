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
import el.ka.someapp.databinding.FragmentToMeasuringBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.NodesViewModel
import el.ka.someapp.viewmodel.TOViewModel
import java.util.*

class TOMeasuringFragment : BaseFragment() {
  private lateinit var binding: FragmentToMeasuringBinding

  private val nodesViewModel: NodesViewModel by activityViewModels()
  private var mTOViewModel: TOViewModel? = null

  private var stateObserver = androidx.lifecycle.Observer<State> {
    if (it != State.LOADING) hideLoadingDialog()
    when (it) {
      State.LOADING -> showLoadingDialog()
      State.BACK -> goBack()
      else -> {}
    }
  }

  override fun initFunctionalityParts() {
    binding = FragmentToMeasuringBinding.inflate(layoutInflater)
    mTOViewModel = ViewModelProvider(this)[TOViewModel::class.java]
    mTOViewModel!!.loadMeasuring(
      measuring = nodesViewModel.currentMeasuring.value!!,
      viewerRole = nodesViewModel.currentRole.value!!
    )
  }

  override fun inflateBindingVariables() {
    binding.apply {
      master = this@TOMeasuringFragment
      lifecycleOwner = viewLifecycleOwner
      viewModel = mTOViewModel
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

    mTOViewModel!!.state.observe(viewLifecycleOwner, stateObserver)

    val hasAccess = hasRole(mTOViewModel!!.viewerRole.value!!, AccessType.EDIT_MEASURING)
    if (hasAccess) {
      // region Date Pickers Listeners
      binding.datePickerLast.setOnClickListener { showDatePicker(DateType.LAST) }
      binding.datePickerNext.setOnClickListener { showDatePicker(DateType.NEXT) }
      // endregion
    }
  }

  override fun onStop() {
    super.onStop()
    mTOViewModel!!.state.removeObserver(stateObserver)

    binding.datePickerLast.setOnClickListener(null)
    binding.datePickerNext.setOnClickListener(null)
  }

  fun trySaveMeasuring() {
    mTOViewModel!!.saveMeasuring {
      nodesViewModel.updateMeasuringPart(MeasuringPart.TO, it)
    }
  }

  // region Date Picker Dialog
  private val datePickerListener = object : Companion.DatePickerListener {
    override fun onPick(date: Date) {
      mTOViewModel!!.saveDate(date)
    }
  }

  private fun showDatePicker(type: DateType) {
    mTOViewModel!!.setEditTime(type)
    val date = when (type) {
      DateType.LAST -> mTOViewModel!!.lastDate.value
      DateType.NEXT -> mTOViewModel!!.nextDate.value
      else -> null
    }
    showDatePickerDialog(date, datePickerListener)
  }
  // endregion
}