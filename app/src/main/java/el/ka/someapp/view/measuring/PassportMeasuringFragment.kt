package el.ka.someapp.view.measuring

import androidx.core.content.ContextCompat
import el.ka.someapp.R
import el.ka.someapp.data.model.SpinnerItem
import el.ka.someapp.data.model.measuring.*
import el.ka.someapp.databinding.FragmentPassportMeasuringBinding
import el.ka.someapp.general.addListener
import el.ka.someapp.view.adapters.MeasuringValueAdapter
import el.ka.someapp.viewmodel.PassportViewModel

class PassportMeasuringFragment : MeasuringPartFragment(MeasuringPart.PASSPORT) {
  private lateinit var measuringValueAdapter: MeasuringValueAdapter
  private lateinit var binding: FragmentPassportMeasuringBinding
  private lateinit var passportViewModel: PassportViewModel

  override fun initFunctionalityParts() {
    measuringValueAdapter = MeasuringValueAdapter()
    super.initFunctionalityParts()
    passportViewModel = viewModel as PassportViewModel
    binding = viewDateBinding as FragmentPassportMeasuringBinding

    measuringValueAdapter.setItems(passportViewModel.measurementValue.value!!)

    binding.measuringValueAdapter =
      measuringValueAdapter

    createSpinners()
  }

  override fun updateAccessToEditFields() {
    super.updateAccessToEditFields()
    measuringValueAdapter.setAccessToEdit(hasAccess)
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
      MeasuringPassportPart.KIND -> passportViewModel.measuringKind.value
      MeasuringPassportPart.CATEGORY -> passportViewModel.measuringCategory.value
      MeasuringPassportPart.MEASUREMENT_TYPE -> passportViewModel.measurementType.value
      MeasuringPassportPart.STATUS -> passportViewModel.measuringState.value
      MeasuringPassportPart.CONDITION -> passportViewModel.conditionDate.value
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


    if (hasAccess) {
      // region Date Pickers Listeners
      binding.datePickerCondition.setOnClickListener { showDatePicker(DateType.CONDITION) }
      binding.datePickerCommission.setOnClickListener { showDatePicker(DateType.COMMISSION) }
      binding.datePickerRelease.setOnClickListener { showDatePicker(DateType.RELEASE) }
      // endregion

      // region Spinners Listeners
      binding.spinnerMeasurementKind.addListener {
        val kind = (it as SpinnerItem).value as MeasuringKind
        passportViewModel.setMeasuringKind(kind)
      }

      binding.spinnerMeasurementCategory.addListener {
        val category = (it as SpinnerItem).value as MeasuringCategory
        passportViewModel.setCategory(category)
      }

      binding.spinnerMeasurementStatus.addListener {
        val state = (it as SpinnerItem).value as MeasuringState
        passportViewModel.setMeasuringState(state)
      }

      binding.spinnerMeasurementCondition.addListener {
        val condition = (it as SpinnerItem).value as MeasuringCondition
        passportViewModel.setMeasuringCondition(condition)
      }

      binding.spinnerMeasurementType.addListener {
        val type = (it as SpinnerItem).value as MeasurementType
        passportViewModel.setMeasurementType(type)
      }
    }
  }

  override fun onStop() {
    super.onStop()
    binding.spinnerMeasurementKind.onItemSelectedListener = null
    binding.spinnerMeasurementCategory.onItemSelectedListener = null
    binding.spinnerMeasurementStatus.onItemSelectedListener = null
    binding.spinnerMeasurementCondition.onItemSelectedListener = null
    binding.spinnerMeasurementType.onItemSelectedListener = null
  }

  override fun allFieldsRight(): Boolean {
    var errors = 0
    val isRequireString = getString(R.string.is_required)

    // Наиминование: обязательное && length >= 3
    val name = passportViewModel.name.value
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
    val measuringKind = passportViewModel.measuringKind.value
    var layout = binding.labelSpinnerKind
    if (checkSpinner(layout, measuringKind, secondaryColor, attentionColor)) errors += 1

    // Категория СИ: обязательное
    val measuringCategory = passportViewModel.measuringCategory.value
    layout = binding.labelSpinnerCategory
    if (checkSpinner(layout, measuringCategory, secondaryColor, attentionColor)) errors += 1

    // Тип: обязательно
    val measuringType = passportViewModel.type.value
    val field = binding.layoutMeasuringType
    if (checkIsNoEmpty(field, measuringType, isRequireString)) errors += 1

    // Вид измерений: обязательно
    val measurementType = passportViewModel.measurementType.value
    layout = binding.labelSpinnerMeasurementType
    if (checkSpinner(layout, measurementType, secondaryColor, attentionColor)) errors += 1

    // Статус: обязательно
    val measuringState = passportViewModel.measuringState.value
    layout = binding.labelSpinnerMeasurementStatus
    if (checkSpinner(layout, measuringState, secondaryColor, attentionColor)) errors += 1
//    if (checkIsNoEmpty(layout, measuringState, isRequireString)) hasErrors = true

    // Состояние: обязательно
    val measuringCondition = passportViewModel.measuringCondition.value
    layout = binding.labelSpinnerMeasurementCondition
    if (checkSpinner(layout, measuringCondition, secondaryColor, attentionColor)) errors += 1

    if (!measuringValueAdapter.checkAllFields()) errors += 1

    return errors == 0
  }
}