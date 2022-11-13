package el.ka.someapp.view

import android.app.Activity
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import el.ka.someapp.R
import el.ka.someapp.data.model.ErrorApp

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


  companion object {
    const val sharedPreferencesName = "METROLOGY"
    const val LOCAL_CURRENT_PASSWORD = "local_current_password"

  }
}