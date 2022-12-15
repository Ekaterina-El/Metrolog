package el.ka.someapp.view

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import el.ka.someapp.MainActivity
import el.ka.someapp.R
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.SpinnerItem
import el.ka.someapp.data.model.measuring.Fields
import el.ka.someapp.data.model.measuring.MeasuringPassportPart
import el.ka.someapp.view.adapters.SpinnerAdapter
import el.ka.someapp.view.dialog.ConfirmDialog
import el.ka.someapp.view.dialog.NetworkErrorDialog
import java.util.*

abstract class BaseFragment : Fragment() {

  private lateinit var sharedPreferences: SharedPreferences

  private fun getLoadingDialog(): Dialog {
    val activity = requireActivity() as MainActivity
    if (activity.loadingDialog == null) createLoadingDialog()
    return activity.loadingDialog!!
  }

  fun showLoadingDialog() {
    val dialog = getLoadingDialog()
    if (!dialog.isShowing) dialog.show()
  }

  fun hideLoadingDialog() {
    val dialog = getLoadingDialog()
    dialog.dismiss()
  }

  private fun createLoadingDialog() {
    val loadingDialog = Dialog(requireContext(), R.style.AppTheme_FullScreenDialog)
    loadingDialog.setContentView(R.layout.fragment_loading_progress_bar)
    loadingDialog.window!!.setLayout(
      LayoutParams.MATCH_PARENT,
      LayoutParams.MATCH_PARENT,
    )
    loadingDialog.window!!.setWindowAnimations(R.style.Slide)
    loadingDialog.setCancelable(false)

    val activity = requireActivity() as MainActivity
    activity.loadingDialog = loadingDialog
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
  private var confirmDialog: ConfirmDialog? = null

  fun openConfirmDialog(
    message: String,
    confirmListener: ConfirmDialog.Companion.ConfirmListener,
    value: Any? = null
  ) {
    if (confirmDialog == null) confirmDialog = ConfirmDialog.getInstance(requireContext())
    confirmDialog!!.openConfirmDialog(message, confirmListener, value)
  }

  fun closeConfirmDialog() {
    confirmDialog?.closeConfirmDialog()
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
    val adapter =
      SpinnerAdapter(requireContext(), getSpinnerItems(itemsArrayRes, arrayValues))

    spinner.adapter = adapter
    adapter.selectItem(value, spinner)
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

  // region Internet Connection
  val isNetworkConnected: Boolean
    get() {
      val connectivityManager =
        requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      val activeNetwork = connectivityManager.activeNetwork

      val hasInternet = activeNetwork != null
      return hasInternet
    }
  // endregion

  // region Network Error Dialog
  private var networkErrorDialog: NetworkErrorDialog? = null

  fun showNetworkErrorDialog() {
    if (networkErrorDialog == null) networkErrorDialog =
      NetworkErrorDialog.getInstance(requireContext())
    networkErrorDialog!!.showDialog()
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

  fun openExcelDocument(path: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    val uri = Uri.parse(path)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    intent.setDataAndType(uri, "application/vnd.ms-excel")
    startActivity(intent)
  }
}