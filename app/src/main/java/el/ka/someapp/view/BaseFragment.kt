package el.ka.someapp.view

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import el.ka.someapp.R
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.SpinnerItem
import el.ka.someapp.data.model.measuring.Fields
import el.ka.someapp.data.model.measuring.MeasurementType
import el.ka.someapp.data.model.measuring.MeasuringPassportPart
import el.ka.someapp.view.adapters.SpinnerAdapter
import el.ka.someapp.view.measuring.PassportMeasuringFragment
import org.w3c.dom.Text
import java.util.*

abstract class BaseFragment : Fragment() {

  private lateinit var sharedPreferences: SharedPreferences

  private var loadingDialog: Dialog? = null

  fun showLoadingDialog() {
    if (loadingDialog == null) createLoadingDialog()
    if (!loadingDialog!!.isShowing) loadingDialog!!.show()
  }

  fun hideLoadingDialog() {
    loadingDialog?.dismiss()
  }

  private fun createLoadingDialog() {
    loadingDialog = Dialog(requireContext(), R.style.AppTheme_FullScreenDialog)
    loadingDialog?.let { loadingDialog ->
      loadingDialog.setContentView(R.layout.fragment_loading_progress_bar)
      loadingDialog.window!!.setLayout(
        LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT,
        )
      loadingDialog.window!!.setWindowAnimations(R.style.Slide)
      loadingDialog.setCancelable(false)
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    sharedPreferences = requireActivity()
      .getSharedPreferences(sharedPreferencesName, Activity.MODE_PRIVATE)
    initFunctionalityParts()
    inflateBindingVariables()
    addOnBackPressButton()
    return null
  }

  abstract fun initFunctionalityParts()
  abstract fun inflateBindingVariables()
  abstract fun onBackPressed()

  private fun addOnBackPressButton() {
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      onBackPressed()
    }
  }

  fun navigate(actionId: Int) {
    Navigation
      .findNavController(requireView())
      .navigate(actionId)
  }

  fun navigate(action: NavDirections) {
    Navigation
      .findNavController(requireView())
      .navigate(action)
  }

  fun popUp() {
    Navigation
      .findNavController(requireView())
      .popBackStack()
  }

  fun getCurrentPassword() = sharedPreferences.getString(LOCAL_CURRENT_PASSWORD, "") ?: ""

  fun setPassword(password: String?) {
    sharedPreferences.edit().putString(LOCAL_CURRENT_PASSWORD, password).apply()
  }

  // region ShowErrorDialog
  private var errorDialog: MaterialAlertDialogBuilder? = null

  private fun createErrorDialog() {
    errorDialog = MaterialAlertDialogBuilder(requireContext())
      .setTitle(resources.getString(R.string.error))
      .setCancelable(false)
      .setNeutralButton(getString(R.string.ok_pin_error)) { dialog, _ ->
        dialog.dismiss()
      }
  }

  fun showErrorDialog(errorApp: ErrorApp) {
    if (errorDialog == null) createErrorDialog()
    errorDialog!!.setMessage(resources.getString(errorApp.textId))
    errorDialog!!.show()
  }
  // endregion

  // region Confirm Dialog
  private var confirmDialog: Dialog? = null
  private lateinit var confirmDialogMessage: TextView
  private lateinit var confirmDialogOk: Button
  private lateinit var confirmDialogCancel: Button

  private fun createConfirmDialog() {
    confirmDialog = Dialog(requireContext())
    confirmDialog?.let { dialog ->
      dialog.setContentView(R.layout.confirm_dialog)
      dialog.window!!.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
      dialog.window!!.setWindowAnimations(R.style.Slide)
      dialog.setCancelable(true)

      confirmDialogMessage = dialog.findViewById<TextView>(R.id.textViewMessage)
      confirmDialogOk = dialog.findViewById<Button>(R.id.buttonYes)
      confirmDialogCancel = dialog.findViewById<Button>(R.id.buttonCancel)
    }
  }

  fun openConfirmDialog(message: String, confirmListener: ConfirmListener, value: Any? = null) {
    if (confirmDialog == null) createConfirmDialog()

    confirmDialogOk.setOnClickListener { confirmListener.onAgree(value) }
    confirmDialogCancel.setOnClickListener { confirmListener.onDisagree() }
    confirmDialogMessage.text = message

    confirmDialog!!.show()
  }

  fun closeConfirmDialog() {
    if (confirmDialog == null) return

    confirmDialogOk.setOnClickListener(null)
    confirmDialogCancel.setOnClickListener(null)
    confirmDialogMessage.text = ""
    confirmDialog!!.dismiss()
  }


  interface ConfirmListener {
    fun onAgree(value: Any? = null)
    fun onDisagree()
  }
  // endregion

  // region Date Picker
  private var datePickerDialog: DatePickerDialog? = null
  private var datePickerListener: DatePickerListener? = null
  private var isOkayClicked = false

  fun showDatePickerDialog(date: Date? = null, listener: DatePickerListener?) {
    if (datePickerDialog == null) createDatePickerDialog()
    updateDatePickerDate(date)

    datePickerListener = listener
    datePickerDialog!!.show()
  }

  private fun createDatePickerDialog() {
    val calendar = Calendar.getInstance()

    val datePickerListener =
      DatePickerDialog.OnDateSetListener { _, year, m, day ->
        if (isOkayClicked) {
          calendar.set(year, m, day)

          val date = calendar.time
          datePickerListener?.onPick(date)

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

  private fun updateDatePickerDate(date: Date?) {
    val cal = Calendar.getInstance()
    if (date != null) cal.time = date

    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val day = cal.get(Calendar.DAY_OF_MONTH)

    datePickerDialog!!.datePicker.updateDate(year, month, day)
  }
  // endregion

  // region Spinner
  fun createSpinner(
    itemsArrayRes: Int,
    arrayValues: List<Any>,
    spinner: Spinner,
    value: Any?
  ) {
    val measurementTypeAdapter =
      SpinnerAdapter(requireContext(), getSpinnerItems(itemsArrayRes, arrayValues))

    spinner.adapter = measurementTypeAdapter
    measurementTypeAdapter.selectItem(value, spinner)
  }

  private fun getSpinnerItems(arrayId: Int, types: List<Any>) =
    resources.getStringArray(arrayId).mapIndexed { idx, s -> SpinnerItem(s, types[idx]) }


  fun getPartVariants(part: MeasuringPassportPart) =
    when (part) {
      MeasuringPassportPart.KIND -> R.array.measuringTypes
      MeasuringPassportPart.CATEGORY -> R.array.measuringCategories
      MeasuringPassportPart.MEASUREMENT_TYPE -> R.array.measurementType
      MeasuringPassportPart.STATUS -> R.array.measurementStatus
      MeasuringPassportPart.CONDITION -> R.array.measurementCondition
    }

  fun getPartValues(part: MeasuringPassportPart) =
    when (part) {
      MeasuringPassportPart.KIND -> Fields.measuringTypeVariables
      MeasuringPassportPart.CATEGORY -> Fields.measuringCategoryVariables
      MeasuringPassportPart.MEASUREMENT_TYPE -> Fields.measurementTypeVariables
      MeasuringPassportPart.STATUS -> Fields.measurementStatusVariables
      MeasuringPassportPart.CONDITION -> Fields.measurementConditionVariables
    }
  // endregion

  companion object {
    const val sharedPreferencesName = "METROLOGY"
    const val LOCAL_CURRENT_PASSWORD = "local_current_password"

    interface DatePickerListener {
      fun onPick(date: Date)
    }

    fun checkIsNoEmpty(
      layout: TextInputLayout,
      value: Any?,
      isRequireString: String
    ): Boolean {
      return if (value == null || value == "") {
        layout.error = isRequireString
        true
      } else {
        layout.error = null
        false
      }
    }

    fun checkSpinner(
      layout: TextView,
      value: Any?,
      secondaryColor: Int,
      attentionColor: Int
    ): Boolean {
      val isNull = value == null
      if (isNull) {
        layout.setTextColor(attentionColor)
      } else {
        layout.setTextColor(secondaryColor)
      }
      return isNull
    }
  }
}