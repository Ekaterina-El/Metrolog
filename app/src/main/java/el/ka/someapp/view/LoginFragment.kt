package el.ka.someapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import el.ka.someapp.R
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.State.AWAITING
import el.ka.someapp.data.model.State.LOADING
import el.ka.someapp.databinding.FragmentLoginBinding
import el.ka.someapp.view.adapters.ErrorAdapter
import el.ka.someapp.viewmodel.LoginViewModel


class LoginFragment : BaseFragment() {
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
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  private val stateObserver = Observer<State> {
    if (it == AWAITING) {
      addObserverToNotifications()
      Toast.makeText(requireContext(), getString(R.string.successAuth), Toast.LENGTH_SHORT).show()
      navigate(R.id.action_loginFragment_to_defenderFragment)
      viewModel.toViewState()
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.errors.observe(viewLifecycleOwner, errorsObserver)
    viewModel.state.observe(viewLifecycleOwner, stateObserver)
  }

  override fun initFunctionalityParts() {
    viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
    binding = FragmentLoginBinding.inflate(layoutInflater)
    errorAdapter = ErrorAdapter()
  }

  override fun inflateBindingVariables() {
    binding.lifecycleOwner = viewLifecycleOwner
    binding.master = this
    binding.viewModel = viewModel
    binding.errorAdapter = errorAdapter
  }

  override fun onBackPressed() {
    if (viewModel.state.value != LOADING) Navigation.findNavController(requireView())
      .popBackStack()
  }

  fun login() {
    viewModel.verificationCredentials()
  }

  fun resetPassword() {
    navigate(R.id.action_loginFragment_to_resetPasswordFragment)
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.errors.removeObserver(errorsObserver)
    viewModel.state.removeObserver(stateObserver)
  }
}