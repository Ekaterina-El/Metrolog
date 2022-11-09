package el.ka.someapp.view.node

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.R
import el.ka.someapp.data.model.measuring.Fields
import el.ka.someapp.data.model.measuring.MeasuringType
import el.ka.someapp.databinding.FragmentAddMeasuringBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.AddMeasuringViewModel
import java.util.*

class AddMeasuringFragment : BaseFragment() {
  private lateinit var binding: FragmentAddMeasuringBinding
  private lateinit var viewModel: AddMeasuringViewModel

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

  private fun createDatePickerDialog() {
    val calendar = Calendar.getInstance()

    val datePickerListener =
      DatePickerDialog.OnDateSetListener { datePicker, year, m, day ->

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, m)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val date = calendar.time

        val month = m + 1
        val dateString = makeDateString(day, month, year)
        Toast.makeText(requireContext(), dateString, Toast.LENGTH_SHORT).show()
      }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val style = AlertDialog.THEME_HOLO_LIGHT

    datePickerDialog = DatePickerDialog(
      requireContext(),
      style, datePickerListener,
      year, month, day
    )
  }

  private fun makeDateString(day: Int, month: Int, year: Int): String {
    return "$day/$month/$year"
  }

  fun showDatePickerDialog() {
    if (datePickerDialog == null) createDatePickerDialog()
    datePickerDialog!!.show()
  }

  override fun onBackPressed() {
//    viewModel.goBack()
  }

  override fun onResume() {
    super.onResume()
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

  override fun onDestroy() {
    binding.spinnerMeasuringType.onItemClickListener = null
    binding.spinnerMeasuringCategory.onItemClickListener = null
    binding.spinnerMeasurementType.onItemClickListener = null
    binding.spinnerMeasuremingStatus.onItemClickListener = null
    binding.spinnerMeasuremingCondition.onItemClickListener = null
    super.onDestroy()
  }

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
}