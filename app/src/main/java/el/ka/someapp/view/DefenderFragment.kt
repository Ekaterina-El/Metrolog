package el.ka.someapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import el.ka.someapp.R
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.State
import el.ka.someapp.databinding.DefenderFragmentBinding
import el.ka.someapp.viewmodel.DefenderViewModel
import el.ka.someapp.viewmodel.NodesViewModel
import el.ka.someapp.viewmodel.StatePassword

class DefenderFragment : BaseFragment() {
  private lateinit var binding: DefenderFragmentBinding
  private lateinit var viewModel: DefenderViewModel

  private val nodesViewModel: NodesViewModel by activityViewModels()


  private lateinit var bounceAnimation: Animation
  private lateinit var circles: List<View>

  private val fieldObserver = Observer<String> {
    val circlePosition = it.length - 1
    circles.forEach { circle -> circle.clearAnimation() }
    if (circlePosition >= 0) {
      circles[circlePosition].startAnimation(bounceAnimation)
    }
  }

  private val nodeStateObserver = Observer<State> {
    when (it) {
      State.NETWORK_ERROR -> {
        showNetworkErrorDialog()
        nodesViewModel.toViewState()
      }
      else -> {}
    }
  }

  private val stateObserver = Observer<State> {
    when (it) {
      State.ERROR -> {
        viewModel.resumeState()
        errorPin()
      }
      else -> {}
    }
  }

  private fun errorPin() {
    Snackbar.make(requireView(), R.string.error_pin, Snackbar.LENGTH_SHORT).show()
  }

  private val statePasswordObserver = Observer<StatePassword> { state ->
    when (state) {
      StatePassword.AWAITING_WITH_SAVE -> savePasswordAndToCompanies()
      StatePassword.AWAITING -> navigateToCompanies()
      StatePassword.LOGOUT -> logout()
      StatePassword.NEW_WITH_ERROR -> showErrorDialog(Errors.noEqualPassword)
      else -> {}
    }
  }

  private fun navigateToCompanies() {
    navigate(R.id.action_defenderFragment_to_companiesFragment)
  }

  private fun savePasswordAndToCompanies() {
    setPassword(viewModel.field.value)
    navigateToCompanies()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce_anim)
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

    circles = listOf(
      binding.c1, binding.c2,
      binding.c3, binding.c4,
      binding.c5
    )

    viewModel.statePassword.observe(viewLifecycleOwner, statePasswordObserver)
    viewModel.field.observe(viewLifecycleOwner, fieldObserver)
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

  override fun onResume() {
    super.onResume()
    viewModel.state.observe(viewLifecycleOwner, stateObserver)
    nodesViewModel.state.observe(viewLifecycleOwner, nodeStateObserver)
  }

  override fun onStop() {
    super.onStop()
    viewModel.state.removeObserver(stateObserver)
    nodesViewModel.state.removeObserver(nodeStateObserver)
  }


  private fun setCorrectPassword() {
    val currentPassword = getCurrentPassword()
    viewModel.setCorrectPassword(currentPassword)
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.statePassword.removeObserver { statePasswordObserver }
    viewModel.field.removeObserver { fieldObserver }
  }

  private fun logout() {
    setPassword(null)
    navigate(R.id.action_defenderFragment_to_welcomeFragment)
  }

  // region Network Error Dialog
  private fun showNetworkErrorDialog() {

  }
  // endregion
}