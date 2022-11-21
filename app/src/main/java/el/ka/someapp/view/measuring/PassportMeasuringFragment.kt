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
import el.ka.someapp.data.model.measuring.DateType
import el.ka.someapp.data.model.measuring.Fields
import el.ka.someapp.data.model.measuring.MeasurementType
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
    createMeasurementKindSpinner()
    createMeasurementTypeSpinner()
  }

  private fun createMeasurementKindSpinner() {

  }


  private fun createMeasurementTypeSpinner() {
    createSpinner(
      R.array.measurementType,
      Fields.measurementTypeVariables,
      binding.spinnerMeasurementType, passportViewModel!!.measurementType.value!!
    )
  }
  // endregion

  override fun onResume() {
    super.onResume()

    // TODO: если нет доступа на изменения не добавлять

    binding.spinnerMeasuringType.setOnItemClickListener { _, _, position, _ ->
      onMeasuringTypeSelected(position)
    }

    binding.spinnerMeasuringCategory.setOnItemClickListener { _, _, position, _ ->
      onMeasuringCategorySelected(position)
    }

    binding.spinnerMeasurementType.onItemSelectedListener =
      object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
          val type = p0!!.selectedItem as SpinnerItem
          onMeasurementTypeSelected(type.value as MeasurementType)
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {}
      }

    binding.spinnerMeasuremingStatus.setOnItemClickListener { _, _, position, _ ->
      onMeasurementStateSelected(position)
    }

    binding.spinnerMeasuremingCondition.setOnItemClickListener { _, _, position, _ ->
      onMeasurementConditionSelected(position)
    }
  }

  // region SpinnerListeners
  private fun onMeasuringTypeSelected(position: Int) {
    val type = Fields.measuringTypeVariables[position]
    passportViewModel!!.setMeasuringKind(type)
  }

  private fun onMeasuringCategorySelected(position: Int) {
    val category = Fields.measuringCategoryVariables[position]
    passportViewModel!!.setCategory(category)
  }

  private fun onMeasurementTypeSelected(type: MeasurementType) {
    passportViewModel!!.setMeasurementType(type)
  }

  private fun onMeasurementStateSelected(position: Int) {
    val status = Fields.measurementStatusVariables[position]
    passportViewModel!!.setMeasuringState(status)
  }

  private fun onMeasurementConditionSelected(position: Int) {
    val condition = Fields.measurementConditionVariables[position]
    passportViewModel!!.setMeasuringCondition(condition)
  }
  // endregion

  fun addMeasuringValue() {

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