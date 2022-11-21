package el.ka.someapp.view.measuring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.R
import el.ka.someapp.data.model.measuring.DateType
import el.ka.someapp.data.model.measuring.Fields
import el.ka.someapp.databinding.FragmentAddMeasuringBinding
import el.ka.someapp.databinding.FragmentPassportMeasuringBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.view.adapters.MeasuringValueAdapter
import el.ka.someapp.viewmodel.NodesViewModel
import el.ka.someapp.viewmodel.PassportViewModel

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
    updateSpinners()
  }

  private fun updateSpinners() {
    val passport = passportViewModel!!.passport

    val measurementTypePos = Fields.measurementTypeVariables.indexOf(passport.measurementType)
    binding.spinnerMeasurementType.setText("123")
    val b = 10
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

  override fun onResume() {
    super.onResume()

    // TODO: если нет доступа на изменения не добавлять

    binding.spinnerMeasuringType.setOnItemClickListener { _, _, position, _ ->
      onMeasuringTypeSelected(position)
    }

    binding.spinnerMeasuringCategory.setOnItemClickListener { _, _, position, _ ->
      onMeasuringCategorySelected(position)
    }

    binding.spinnerMeasurementType.setOnItemClickListener { _, _, position, _ ->
      onMeasurementTypeSelected(position)
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

  private fun onMeasurementTypeSelected(position: Int) {
    val type = Fields.measurementTypeVariables[position]
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



  fun showDatePicker(dateType: DateType) {
  }

  fun addMeasuringValue() {

  }

  fun trySaveMeasuring() {

  }

  override fun onDestroy() {
    super.onDestroy()
    passportViewModel = null
  }
}