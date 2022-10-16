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
import el.ka.someapp.databinding.FragmentRegistrationBinding
import el.ka.someapp.view.adapters.ErrorAdapter
import el.ka.someapp.viewmodel.RegistrationViewModel

class RegistrationFragment : BaseFragment() {
  private lateinit var binding: FragmentRegistrationBinding
  private lateinit var viewModel: RegistrationViewModel

  private lateinit var errorAdapter: ErrorAdapter
  private val errorsObserver = Observer<MutableList<ErrorApp>> {
    errorAdapter.setErrors(it)
  }

  private val goToLoginState = Observer<State> {
    if (it == State.AWAITING) {
      Toast.makeText(requireContext(), getString(R.string.successReg), Toast.LENGTH_SHORT).show()
      navigate(R.id.action_registrationFragment_to_loginFragment)
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun initFunctionalityParts() {
    viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]
    binding = FragmentRegistrationBinding.inflate(layoutInflater)
    errorAdapter = ErrorAdapter()
  }

  override fun inflateBindingVariables() {
    binding.master = this
    binding.viewModel = viewModel
    binding.lifecycleOwner = viewLifecycleOwner
    binding.errorAdapter = errorAdapter
  }

  override fun onResume() {
    super.onResume()
    viewModel.errors.observe(viewLifecycleOwner, errorsObserver)
    viewModel.state.observe(viewLifecycleOwner, goToLoginState)
  }

  override fun onBackPressed() {
    if (viewModel.state.value != State.LOADING) Navigation.findNavController(requireView())
      .popBackStack()
  }

  fun registration() {
    viewModel.verificationCredentials()
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.errors.removeObserver { errorsObserver }
    viewModel.state.removeObserver { goToLoginState }
  }
}