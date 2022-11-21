package el.ka.someapp.view.node

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.R
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.measuring.DateType
import el.ka.someapp.data.model.measuring.Fields
import el.ka.someapp.databinding.FragmentAddMeasuringBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.view.adapters.MeasuringValueAdapter
import el.ka.someapp.viewmodel.AddMeasuringViewModel
import el.ka.someapp.viewmodel.NodesViewModel
import el.ka.someapp.viewmodel.VisibleViewModel
import java.util.Calendar
import java.util.Date


class AddMeasuringFragment : BaseFragment() {
  private val visibleViewModel: VisibleViewModel by activityViewModels()
  private lateinit var binding: FragmentAddMeasuringBinding

  private lateinit var viewModel: AddMeasuringViewModel
  private val stateObserver = Observer<State> { state ->
    if (state == State.LOADING) showLoadingDialog() else hideLoadingDialog()
    when (state) {
      State.MEASURING_ADDED -> {
        goBack()
      }
      State.BACK -> {
        goBack()
      }
      else -> {}
    }
  }

  private val nodesViewModel: NodesViewModel by activityViewModels()

  private lateinit var measuringValueAdapter: MeasuringValueAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun initFunctionalityParts() {
    binding = FragmentAddMeasuringBinding.inflate(layoutInflater)

    measuringValueAdapter = MeasuringValueAdapter()
    measuringValueAdapter.addNewItem()

    viewModel = ViewModelProvider(this)[AddMeasuringViewModel::class.java]
  }

  override fun inflateBindingVariables() {
    binding.apply {
      viewmodel = this@AddMeasuringFragment.viewModel
      measuringValueAdapter = this@AddMeasuringFragment.measuringValueAdapter
      lifecycleOwner = viewLifecycleOwner
      master = this@AddMeasuringFragment
    }
  }

  override fun onBackPressed() {
    if (viewModel.state.value != State.LOADING) viewModel.goBack()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.setLocationIDNode(nodesViewModel.currentNode.value!!.id)
  }

  private fun goBack() {
    visibleViewModel.setNodeNavigationState(true)
    navigate(R.id.action_addMeasuringFragment_to_nodeMeasuringFragment)
  }

  fun tryAddMeasuring() {
    if (!checkFields()) {
      binding.textError.visibility = View.GONE
      viewModel.setMeasurementValues(measuringValueAdapter.getMeasuringValues())
      viewModel.saveMeasuring {
        nodesViewModel.addMeasuringToLocal(it)
      }
    } else {
      binding.textError.visibility = View.VISIBLE
    }
  }

  private fun checkFields(): Boolean {
    var hasErrors = false
    val isRequireString = getString(R.string.is_required)

    // Наиминование: обязательное && length >= 3
    val name = viewModel.name.value
    if (name == null || name == "") {
      binding.layoutName.error = isRequireString
      hasErrors = true
    } else if (name.length < 3) {
      binding.layoutName.error = getString(R.string.required_min_length, 3)
      hasErrors = true
    } else {
      binding.layoutName.error = null
    }

    // Вид СИ: обязательное
    val measuringKind = viewModel.measuringKind.value
    var layout = binding.layoutMeasuringKind
    if (checkIsNoEmpty(layout, measuringKind, isRequireString)) hasErrors = true

    // Категория СИ: обязательное
    val measuringCategory = viewModel.measuringCategory.value
    layout = binding.layoutMeasuringCategory
    if (checkIsNoEmpty(layout, measuringCategory, isRequireString)) hasErrors = true

    // Тип: обязательно
    val measuringType = viewModel.type.value
    layout = binding.layoutMeasuringType
    if (checkIsNoEmpty(layout, measuringType, isRequireString)) hasErrors = true

    // Вид измерений: обязательно
    val measurementType = viewModel.measurementType.value
    layout = binding.layoutMeasurementType
    if (checkIsNoEmpty(layout, measurementType, isRequireString)) hasErrors = true

    if (!measuringValueAdapter.checkAllFields()) {
      hasErrors = true
    }


    // Статус: обязательно
    val measuringState = viewModel.measuringState.value
    layout = binding.layoutMeasuringState
    if (checkIsNoEmpty(layout, measuringState, isRequireString)) hasErrors = true

    // Состояние: обязательно
    val measuringCondition = viewModel.measuringCondition.value
    layout = binding.layoutMeasuringCondition
    if (checkIsNoEmpty(layout, measuringCondition, isRequireString)) hasErrors = true

    return hasErrors
  }

  fun addMeasuringValue() {
     measuringValueAdapter.addNewItem()
  }

  override fun onResume() {
    super.onResume()
    // region add spinner listeners
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
    // endregion

    // Add state listener
    viewModel.state.observe(viewLifecycleOwner, stateObserver)
  }

  override fun onStop() {
    // region remove listeners
    binding.spinnerMeasuringType.onItemClickListener = null
    binding.spinnerMeasuringCategory.onItemClickListener = null
    binding.spinnerMeasurementType.onItemClickListener = null
    binding.spinnerMeasuremingStatus.onItemClickListener = null
    binding.spinnerMeasuremingCondition.onItemClickListener = null
    // endregion

    // Remove  state listener
    viewModel.state.removeObserver(stateObserver)

    super.onStop()
  }


  // region Date Picker Dialog
  private val datePickerListener = object: Companion.DatePickerListener {
    override fun onPick(date: Date) {
      viewModel.saveDate(date)
    }

  }

  fun showDatePicker(type: DateType) {
    viewModel.setEditTime(type)
    val date = when (type) {
      DateType.RELEASE -> viewModel.releaseDate.value
      DateType.COMMISSION -> viewModel.commissioningDate.value
      DateType.CONDITION -> viewModel.conditionDate.value
    }
    showDatePickerDialog(date, datePickerListener)
  }
  // endregion Date Picker Dialog

  // region SpinnerListeners
  private fun onMeasuringTypeSelected(position: Int) {
    val type = Fields.measuringTypeVariables[position]
    viewModel.setMeasuringKind(type)
  }

  private fun onMeasuringCategorySelected(position: Int) {
    val category = Fields.measuringCategoryVariables[position]
    viewModel.setCategory(category)
  }

  private fun onMeasurementTypeSelected(position: Int) {
    val type = Fields.measurementTypeVariables[position]
    viewModel.setMeasurementType(type)
  }

  private fun onMeasurementStateSelected(position: Int) {
    val status = Fields.measurementStatusVariables[position]
    viewModel.setMeasuringState(status)
  }

  private fun onMeasurementConditionSelected(position: Int) {
    val condition = Fields.measurementConditionVariables[position]
    viewModel.setMeasuringCondition(condition)
  }
  // endregion
}