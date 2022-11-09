package el.ka.someapp.view.node

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.data.model.measuring.DateType
import el.ka.someapp.data.model.measuring.Fields
import el.ka.someapp.databinding.FragmentAddMeasuringBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.AddMeasuringViewModel
import el.ka.someapp.viewmodel.NodesViewModel
import java.util.*


class AddMeasuringFragment : BaseFragment() {
  private lateinit var binding: FragmentAddMeasuringBinding
  private lateinit var viewModel: AddMeasuringViewModel

  private val nodesViewModel: NodesViewModel by activityViewModels()

  private var datePickerDialog: DatePickerDialog? = null

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
    viewModel = ViewModelProvider(this)[AddMeasuringViewModel::class.java]
  }

  override fun inflateBindingVariables() {
    binding.apply {
      viewmodel = this@AddMeasuringFragment.viewModel
      lifecycleOwner = viewLifecycleOwner
      master = this@AddMeasuringFragment
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.setLocationIDNode(nodesViewModel.currentNode.value!!.id)
  }

  // region Date Picker Dialog
  private var isOkayClicked = false

  private fun createDatePickerDialog() {
    val calendar = Calendar.getInstance()

    val datePickerListener =
      DatePickerDialog.OnDateSetListener { datePicker, year, m, day ->
        if (isOkayClicked) {
          calendar.set(year, m, day)
          val date = calendar.time

          viewModel.saveDate(date)

          val month = m + 1
          val dateString = makeDateString(day, month, year)
          Toast.makeText(requireContext(), dateString, Toast.LENGTH_SHORT).show()
          isOkayClicked = false
        }

      }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    datePickerDialog = DatePickerDialog(requireContext(), datePickerListener, year, month, day)

    datePickerDialog!!.setButton(
      DialogInterface.BUTTON_POSITIVE,
      "OK"
    ) { _, which ->
      if (which == DialogInterface.BUTTON_POSITIVE) {
        isOkayClicked = true
        val datePicker = datePickerDialog!!.datePicker
        datePickerListener.onDateSet(
          datePicker,
          datePicker.year,
          datePicker.month,
          datePicker.dayOfMonth
        )
      }
    }
  }

  private fun makeDateString(day: Int, month: Int, year: Int): String {
    return "$day/$month/$year"
  }

  fun showDatePicker(type: DateType) {
    viewModel.setEditTime(type)
    val date = when(type) {
      DateType.RELEASE -> viewModel.releaseDate.value
      DateType.COMMISSION -> viewModel.commissioningDate.value
      DateType.CONDITION -> viewModel.conditionDate.value
    }
    showDatePickerDialog(date = date)
  }

  private fun showDatePickerDialog(date: Date? = null) {
    if (datePickerDialog == null) createDatePickerDialog()
    updateDatePickerDate(date)

    datePickerDialog!!.show()
  }

  private fun updateDatePickerDate(date: Date?) {
    val cal = Calendar.getInstance()
    if (date != null) cal.time = date

    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val day = cal.get(Calendar.DAY_OF_MONTH)

    datePickerDialog!!.datePicker.updateDate(year, month, day)
  }

  // endregion Date Picker Dialog

  override fun onBackPressed() {
//    viewModel.goBack()
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
  }

  override fun onDestroy() {
    // region remove listeners
    binding.spinnerMeasuringType.onItemClickListener = null
    binding.spinnerMeasuringCategory.onItemClickListener = null
    binding.spinnerMeasurementType.onItemClickListener = null
    binding.spinnerMeasuremingStatus.onItemClickListener = null
    binding.spinnerMeasuremingCondition.onItemClickListener = null
    // endregion
    super.onDestroy()
  }

  // region SpinnerListeners
  private fun onMeasuringTypeSelected(position: Int) {
    val type = Fields.measuringTypeVariables[position]
    viewModel.setMeasuringType(type)
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