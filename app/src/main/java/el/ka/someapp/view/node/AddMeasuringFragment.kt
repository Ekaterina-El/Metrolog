package el.ka.someapp.view.node

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
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
}