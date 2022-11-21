package el.ka.someapp.view.measuring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.R
import el.ka.someapp.data.model.SpinnerItem
import el.ka.someapp.data.model.addListener
import el.ka.someapp.data.model.measuring.*
import el.ka.someapp.databinding.FragmentPassportMeasuringBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.view.adapters.MeasuringValueAdapter
import el.ka.someapp.view.adapters.SpinnerAdapter
import el.ka.someapp.viewmodel.NodesViewModel
import el.ka.someapp.viewmodel.PassportViewModel
import java.util.*

class PassportMeasuringFragment : BaseFragment() {
  private lateinit var binding: FragmentPassportMeasuringBinding
  private val nodesViewModel: NodesViewModel by activityViewModels()

  private lateinit var measuringValueAdapter: MeasuringValueAdapter
  private var passportViewModel: PassportViewModel? = null


  override fun initFunctionalityParts() {
    binding = FragmentPassportMeasuringBinding.inflate(layoutInflater)

    passportViewModel = ViewModelProvider(this)[PassportViewModel::class.java]
    passportViewModel!!.loadMeasuring(nodesViewModel.currentMeasuring.value!!)

    measuringValueAdapter = MeasuringValueAdapter()
    measuringValueAdapter.setItems(passportViewModel!!.measurementValue.value!!)
    createSpinners()
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
      MeasuringPassportPart.KIND -> passportViewModel!!.measuringKind
      MeasuringPassportPart.CATEGORY -> passportViewModel!!.measuringCategory
      MeasuringPassportPart.MEASUREMENT_TYPE -> passportViewModel!!.measurementType
      MeasuringPassportPart.STATUS -> passportViewModel!!.measuringState
      MeasuringPassportPart.CONDITION -> passportViewModel!!.conditionDate
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
    // TODO: если нет доступа на изменения не добавлять

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

  }

  override fun onStop() {
    super.onStop()

    binding.spinnerMeasurementKind.onItemSelectedListener = null
    binding.spinnerMeasurementCategory.onItemSelectedListener = null
    binding.spinnerMeasurementStatus.onItemSelectedListener = null
    binding.spinnerMeasurementCondition.onItemSelectedListener = null
    binding.spinnerMeasurementType.onItemSelectedListener = null
  }


  fun addMeasuringValue() {
    measuringValueAdapter.addNewItem()
  }

  fun trySaveMeasuring() {

  }

  override fun onDestroy() {
    super.onDestroy()
    passportViewModel = null
  }

  // region Date Picker Dialog
  private val datePickerListener = object : Companion.DatePickerListener {
    override fun onPick(date: Date) {
      passportViewModel!!.saveDate(date)
    }
  }

  fun showDatePicker(type: DateType) {
    passportViewModel!!.setEditTime(type)
    val date = when (type) {
      DateType.RELEASE -> passportViewModel!!.releaseDate.value
      DateType.COMMISSION -> passportViewModel!!.commissioningDate.value
      DateType.CONDITION -> passportViewModel!!.conditionDate.value
    }
    showDatePickerDialog(date, datePickerListener)
  }
  // endregion Date Picker Dialog
}