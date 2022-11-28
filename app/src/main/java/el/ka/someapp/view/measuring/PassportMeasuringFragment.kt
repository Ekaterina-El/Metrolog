package el.ka.someapp.view.measuring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.R
import el.ka.someapp.data.model.SpinnerItem
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.measuring.*
import el.ka.someapp.data.model.role.AccessType
import el.ka.someapp.data.model.role.hasRole
import el.ka.someapp.databinding.FragmentPassportMeasuringBinding
import el.ka.someapp.general.addListener
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.view.adapters.MeasuringValueAdapter
import el.ka.someapp.viewmodel.NodesViewModel
import el.ka.someapp.viewmodel.PassportViewModel
import java.util.*

class PassportMeasuringFragment : BaseFragment() {
  private lateinit var binding: FragmentPassportMeasuringBinding
  private val nodesViewModel: NodesViewModel by activityViewModels()

  private lateinit var measuringValueAdapter: MeasuringValueAdapter
  private var passportViewModel: PassportViewModel? = null

  private var stateObserver = androidx.lifecycle.Observer<State> {
    if (it != State.LOADING) hideLoadingDialog()

    when (it) {
      State.LOADING -> showLoadingDialog()
      State.BACK -> goBack()
      else -> {}
    }
  }


  override fun initFunctionalityParts() {
    binding = FragmentPassportMeasuringBinding.inflate(layoutInflater)

    passportViewModel = ViewModelProvider(this)[PassportViewModel::class.java]
    passportViewModel!!.loadMeasuring(
      measuring = nodesViewModel.currentMeasuring.value!!,
      viewerRole = nodesViewModel.currentRole.value!!
    )

    measuringValueAdapter = MeasuringValueAdapter()
    measuringValueAdapter.setItems(passportViewModel!!.measurementValue.value!!)

    createSpinners()
    updateAccessToEditFields()
  }

  private fun updateAccessToEditFields() {
    val hasAccess = hasRole(passportViewModel!!.viewerRole.value!!, AccessType.EDIT_MEASURING)

    listOf<View>(
      binding.layoutName,
      binding.spinnerMeasurementKind,
      binding.spinnerMeasurementCategory,
      binding.layoutMeasuringType,
      binding.layoutInventoryName,
      binding.layoutSerialName,
      binding.layoutRegNameGRSI,
      binding.layoutManufacturer,
      binding.layoutSupplier,
      binding.layoutSectorGROEI,
      binding.spinnerMeasurementType,
      binding.spinnerMeasurementStatus,
      binding.spinnerMeasurementCondition,
    ).forEach { it.isEnabled = hasAccess }

    measuringValueAdapter.setAccessToEdit(hasAccess)
  }

  override fun inflateBindingVariables() {
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      viewmodel = passportViewModel
      master = this@PassportMeasuringFragment
      measuringValueAdapter = this@PassportMeasuringFragment.measuringValueAdapter
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

  // region Spinner Adapters
  private fun createSpinners() {
    createSpinner(MeasuringPassportPart.KIND)
    createSpinner(MeasuringPassportPart.CATEGORY)
    createSpinner(MeasuringPassportPart.MEASUREMENT_TYPE)
    createSpinner(MeasuringPassportPart.STATUS)
    createSpinner(MeasuringPassportPart.CONDITION)
  }

  private fun createSpinner(passportPart: MeasuringPassportPart) {
    val array = getPartVariants(passportPart)
    val values = getPartValues(passportPart)
    val spinner = getPartSpinner(passportPart)
    val currentValue = getCurrentValue(passportPart)

    createSpinner(array, values, spinner, currentValue)
  }

  private fun getCurrentValue(part: MeasuringPassportPart) =
    when (part) {
      MeasuringPassportPart.KIND -> passportViewModel!!.measuringKind.value
      MeasuringPassportPart.CATEGORY -> passportViewModel!!.measuringCategory.value
      MeasuringPassportPart.MEASUREMENT_TYPE -> passportViewModel!!.measurementType.value
      MeasuringPassportPart.STATUS -> passportViewModel!!.measuringState.value
      MeasuringPassportPart.CONDITION -> passportViewModel!!.conditionDate.value
    }

  private fun getPartSpinner(part: MeasuringPassportPart) =
    when (part) {
      MeasuringPassportPart.KIND -> binding.spinnerMeasurementKind
      MeasuringPassportPart.CATEGORY -> binding.spinnerMeasurementCategory
      MeasuringPassportPart.MEASUREMENT_TYPE -> binding.spinnerMeasurementType
      MeasuringPassportPart.STATUS -> binding.spinnerMeasurementStatus
      MeasuringPassportPart.CONDITION -> binding.spinnerMeasurementCondition
    }
  // endregion

  override fun onResume() {
    super.onResume()

    passportViewModel!!.state.observe(viewLifecycleOwner, stateObserver)

    val hasAccess = hasRole(passportViewModel!!.viewerRole.value!!, AccessType.EDIT_MEASURING)
    if (hasAccess) {

      // region Date Pickers Listeners
      binding.datePickerCondition.setOnClickListener { showDatePicker(DateType.CONDITION) }
      binding.datePickerCommission.setOnClickListener { showDatePicker(DateType.COMMISSION) }
      binding.datePickerRelease.setOnClickListener { showDatePicker(DateType.RELEASE) }
      // endregion

      // region Spinners Listeners
      binding.spinnerMeasurementKind.addListener {
        val kind = (it as SpinnerItem).value as MeasuringKind
        passportViewModel!!.setMeasuringKind(kind)
      }

      binding.spinnerMeasurementCategory.addListener {
        val category = (it as SpinnerItem).value as MeasuringCategory
        passportViewModel!!.setCategory(category)
      }

      binding.spinnerMeasurementStatus.addListener {
        val state = (it as SpinnerItem).value as MeasuringState
        passportViewModel!!.setMeasuringState(state)
      }

      binding.spinnerMeasurementCondition.addListener {
        val condition = (it as SpinnerItem).value as MeasuringCondition
        passportViewModel!!.setMeasuringCondition(condition)
      }

      binding.spinnerMeasurementType.addListener {
        val type = (it as SpinnerItem).value as MeasurementType
        passportViewModel!!.setMeasurementType(type)
      }
      // endregion
    }

  }

  override fun onStop() {
    super.onStop()

    passportViewModel!!.state.removeObserver(stateObserver)

    binding.spinnerMeasurementKind.onItemSelectedListener = null
    binding.spinnerMeasurementCategory.onItemSelectedListener = null
    binding.spinnerMeasurementStatus.onItemSelectedListener = null
    binding.spinnerMeasurementCondition.onItemSelectedListener = null
    binding.spinnerMeasurementType.onItemSelectedListener = null
  }

  override fun onDestroy() {
    super.onDestroy()
    passportViewModel = null
  }

  fun addMeasuringValue() {
    measuringValueAdapter.addNewItem()
  }

  fun trySaveMeasuring() {
    if (allFieldsRight()) {
      binding.textError.visibility = View.GONE
      passportViewModel!!.setMeasurementValues(measuringValueAdapter.getMeasuringValues())
      passportViewModel!!.saveMeasuring {
        nodesViewModel.updateMeasuringPart(MeasuringPart.PASSPORT, it)
      }
    } else {
      binding.textError.visibility = View.VISIBLE
    }
  }

  private fun allFieldsRight(): Boolean {
    var errors = 0
    val isRequireString = getString(R.string.is_required)

    // Наиминование: обязательное && length >= 3
    val name = passportViewModel!!.name.value
    if (name == null || name == "") {
      binding.layoutName.error = isRequireString
      errors += 1
    } else if (name.length < 3) {
      binding.layoutName.error = getString(R.string.required_min_length, 3)
    } else {
      binding.layoutName.error = null
    }

    val attentionColor = ContextCompat.getColor(requireContext(), R.color.attentionColor)
    val secondaryColor = ContextCompat.getColor(requireContext(), R.color.secondary_color)

    // Вид СИ: обязательное
    val measuringKind = passportViewModel!!.measuringKind.value
    var layout = binding.labelSpinnerKind
    if (checkSpinner(layout, measuringKind, secondaryColor, attentionColor)) errors += 1

    // Категория СИ: обязательное
    val measuringCategory = passportViewModel!!.measuringCategory.value
    layout = binding.labelSpinnerCategory
    if (checkSpinner(layout, measuringCategory, secondaryColor, attentionColor)) errors += 1

    // Тип: обязательно
    val measuringType = passportViewModel!!.type.value
    val field = binding.layoutMeasuringType
    if (checkIsNoEmpty(field, measuringType, isRequireString)) errors += 1
//    if (checkIsNoEmpty(layout, measuringType, isRequireString)) hasErrors = true

    // Вид измерений: обязательно
    val measurementType = passportViewModel!!.measurementType.value
    layout = binding.labelSpinnerMeasurementType
    if (checkSpinner(layout, measurementType, secondaryColor, attentionColor)) errors += 1

    // Статус: обязательно
    val measuringState = passportViewModel!!.measuringState.value
    layout = binding.labelSpinnerMeasurementStatus
    if (checkSpinner(layout, measuringState, secondaryColor, attentionColor)) errors += 1
//    if (checkIsNoEmpty(layout, measuringState, isRequireString)) hasErrors = true

    // Состояние: обязательно
    val measuringCondition = passportViewModel!!.measuringCondition.value
    layout = binding.labelSpinnerMeasurementCondition
    if (checkSpinner(layout, measuringCondition, secondaryColor, attentionColor)) errors += 1

    if (!measuringValueAdapter.checkAllFields()) errors += 1

    return errors == 0
  }

  // region Date Picker Dialog
  private val datePickerListener = object : Companion.DatePickerListener {
    override fun onPick(date: Date) {
      passportViewModel!!.saveDate(date)
    }
  }

  private fun showDatePicker(type: DateType) {
    passportViewModel!!.setEditTime(type)
    val date = when (type) {
      DateType.RELEASE -> passportViewModel!!.releaseDate.value
      DateType.COMMISSION -> passportViewModel!!.commissioningDate.value
      DateType.CONDITION -> passportViewModel!!.conditionDate.value
      else -> null
    }
    showDatePickerDialog(date, datePickerListener)
  }
  // endregion Date Picker Dialog
}