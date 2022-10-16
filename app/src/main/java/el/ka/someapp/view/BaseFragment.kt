package el.ka.someapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

abstract class BaseFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
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
}