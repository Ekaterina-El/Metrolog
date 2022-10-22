package el.ka.someapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.R
import el.ka.someapp.databinding.DefenderFragmentBinding
import el.ka.someapp.viewmodel.DefenderViewModel
import el.ka.someapp.viewmodel.StatePassword

class DefenderFragment : BaseFragment() {
  private lateinit var binding: DefenderFragmentBinding
  private lateinit var viewModel: DefenderViewModel

  private val statePasswordObserver = Observer<StatePassword> { state ->
    when (state) {
      StatePassword.AWAITING_WITH_SAVE -> savePasswordAndToCompanies()
      StatePassword.AWAITING -> navigateToCompanies()
      StatePassword.LOGOUT -> logout()
      else -> {}
    }
  }

  private fun navigateToCompanies() {
    navigate(R.id.action_defenderFragment_to_companiesFragment)
  }

  private fun savePasswordAndToCompanies() {
    setPassword(viewModel.field.value)
    //sharedPreferences.edit().putString(LOCAL_CURRENT_PASSWORD, viewModel.field.value).apply()
    navigateToCompanies()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    setCorrectPassword()
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.statePassword.observe(viewLifecycleOwner, statePasswordObserver)
  }

  override fun initFunctionalityParts() {
    binding = DefenderFragmentBinding.inflate(layoutInflater)
    viewModel = ViewModelProvider(this)[DefenderViewModel::class.java]
  }

  override fun inflateBindingVariables() {
    binding.apply {
      master = this@DefenderFragment
      lifecycleOwner = viewLifecycleOwner
      viewModel = this@DefenderFragment.viewModel
    }
  }

  override fun onBackPressed() {}


  private fun setCorrectPassword() {
    val currentPassword = getCurrentPassword()
    viewModel.setCorrectPassword(currentPassword)
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.statePassword.removeObserver { statePasswordObserver }
  }

  private fun logout() {
    setPassword(null)
    navigate(R.id.action_defenderFragment_to_welcomeFragment)
  }
}