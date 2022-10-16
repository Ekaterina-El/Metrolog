package el.ka.someapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import el.ka.someapp.R
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.State.*
import el.ka.someapp.databinding.FragmentLoginBinding
import el.ka.someapp.view.adapters.ErrorAdapter
import el.ka.someapp.viewmodel.LoginViewModel


class LoginFragment : Fragment() {
  private lateinit var binding: FragmentLoginBinding
  private lateinit var viewModel: LoginViewModel

  private lateinit var errorAdapter: ErrorAdapter
  private val errorsObserver = Observer<MutableList<ErrorApp>> {
    errorAdapter.setErrors(it)
  }

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

  private val goToDefenderState = Observer<State> {
    if (it == AWAITING) {
      Toast.makeText(requireContext(), getString(R.string.successAuth), Toast.LENGTH_SHORT).show()
      goToDefenderState()
    }
  }

  private fun goToDefenderState() {
    Navigation
      .findNavController(requireView())
      .navigate(R.id.action_loginFragment_to_defenderFragment)
  }

  override fun onResume() {
    super.onResume()
    viewModel.errors.observe(viewLifecycleOwner, errorsObserver)
    viewModel.state.observe(viewLifecycleOwner, goToDefenderState)
  }

  private fun initFunctionalityParts() {
    viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
    binding = FragmentLoginBinding.inflate(layoutInflater)
    errorAdapter = ErrorAdapter()
  }

  private fun inflateBindingVariables() {
    binding.lifecycleOwner = viewLifecycleOwner
    binding.master = this
    binding.viewModel = viewModel
    binding.errorAdapter = errorAdapter
  }

  private fun addOnBackPressButton() {
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      if (viewModel.state.value != LOADING) Navigation.findNavController(requireView())
        .popBackStack()
    }
  }

  fun login() {
    viewModel.verificationCredentials()
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.errors.removeObserver(errorsObserver)
    viewModel.state.removeObserver(goToDefenderState)
  }
}