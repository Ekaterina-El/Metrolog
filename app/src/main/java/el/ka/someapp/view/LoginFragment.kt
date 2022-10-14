package el.ka.someapp.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import el.ka.someapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
  var isLoading = false
  private lateinit var binding: FragmentLoginBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentLoginBinding.inflate(layoutInflater)
    binding.master = this
    return binding.root
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addOnBackPressButton()
  }

  private fun addOnBackPressButton() {
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      if (!isLoading) Navigation.findNavController(requireView()).popBackStack()
    }
  }

  fun login() {
    isLoading = true
    binding.layoutLoader.visibility = View.VISIBLE    // TODO: in future move to viewmodel
  }
}