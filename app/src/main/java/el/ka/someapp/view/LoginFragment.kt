package el.ka.someapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import el.ka.someapp.data.State.*
import el.ka.someapp.databinding.FragmentLoginBinding
import el.ka.someapp.viewmodel.LoginViewModel


class LoginFragment : Fragment() {
  private lateinit var binding: FragmentLoginBinding
  private lateinit var viewModel: LoginViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    initFunctionalityParts()
    inflateBindingVariables()
    addOnBackPressButton()
    return binding.root
  }

  private fun initFunctionalityParts() {
    viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
    binding = FragmentLoginBinding.inflate(layoutInflater)
  }

  private fun inflateBindingVariables() {
    binding.lifecycleOwner = viewLifecycleOwner
    binding.master = this
    binding.viewModel = viewModel
  }

  private fun addOnBackPressButton() {
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      if (viewModel.state.value != LOADING) Navigation.findNavController(requireView())
        .popBackStack()
    }
  }

  fun login() {
    verificationCredentials()
  }

  private fun verificationCredentials() {
    val email = viewModel.email.value
    val password = viewModel.password.value
    Toast.makeText(requireContext(), "$email : $password", Toast.LENGTH_SHORT).show()
  }
}