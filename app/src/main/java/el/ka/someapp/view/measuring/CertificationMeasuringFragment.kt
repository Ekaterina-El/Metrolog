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
import el.ka.someapp.databinding.FragmentCertificationMeasuringBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.CertificationMeasuringViewModel
import el.ka.someapp.viewmodel.NodesViewModel
import java.util.*

class CertificationMeasuringFragment : BaseFragment() {
  private lateinit var binding: FragmentCertificationMeasuringBinding

  private val nodesViewModel: NodesViewModel by activityViewModels()
  private var viewModel: CertificationMeasuringViewModel? = null

  private var stateObserver = androidx.lifecycle.Observer<State> {
    if (it != State.LOADING) hideLoadingDialog()
    when (it) {
      State.LOADING -> showLoadingDialog()
      State.BACK -> goBack()
      else -> {}
    }
  }

  override fun initFunctionalityParts() {
    binding = FragmentCertificationMeasuringBinding.inflate(layoutInflater)
    viewModel =
      ViewModelProvider(this)[CertificationMeasuringViewModel::class.java]

    viewModel!!.loadMeasuring(
      measuring = nodesViewModel.currentMeasuring.value!!,
      viewerRole = nodesViewModel.currentRole.value!!
    )
    updateAccessToEditFields()
  }

  override fun inflateBindingVariables() {
    binding.apply {
      master = this@CertificationMeasuringFragment
      lifecycleOwner = viewLifecycleOwner
      viewModel = this@CertificationMeasuringFragment.viewModel
    }
  }

  private fun updateAccessToEditFields() {
    val hasAccess =
      hasRole(viewModel!!.viewerRole.value!!, AccessType.EDIT_MEASURING)

    listOf<View>(
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
    viewModel!!.state.observe(viewLifecycleOwner, stateObserver)

    val hasAccess =
      hasRole(viewModel!!.viewerRole.value!!, AccessType.EDIT_MEASURING)
    if (hasAccess) {
      // region Date Pickers Listeners
      binding.datePickerLast.setOnClickListener { showDatePicker(DateType.LAST) }
      binding.datePickerNext.setOnClickListener { showDatePicker(DateType.NEXT) }
      // endregion
    }
  }

  override fun onStop() {
    super.onStop()
    viewModel!!.state.removeObserver(stateObserver)

    binding.datePickerLast.setOnClickListener(null)
    binding.datePickerNext.setOnClickListener(null)
  }

  fun trySaveMeasuring() {
    viewModel!!.saveMeasuring {
      nodesViewModel.updateMeasuringPart(MeasuringPart.CERTIFICATION, it)
    }
  }

  private val datePickerListener = object : Companion.DatePickerListener {
    override fun onPick(date: Date) {
      viewModel!!.saveDate(date)
    }
  }

  // region Date Picker Dialog
  private fun showDatePicker(type: DateType) {
    viewModel!!.setEditTime(type)
    val date = when (type) {
      DateType.LAST -> viewModel!!.lastDate.value
      DateType.NEXT -> viewModel!!.nextDate.value
      else -> null
    }
    showDatePickerDialog(date, datePickerListener)
  }
  // endregion
}