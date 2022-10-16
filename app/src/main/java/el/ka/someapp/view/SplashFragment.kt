package el.ka.someapp.view

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.R
import el.ka.someapp.databinding.SplashFragmentBinding
import el.ka.someapp.viewmodel.SplashViewModel
import el.ka.someapp.viewmodel.UserState

class SplashFragment : BaseFragment() {
  private lateinit var viewModel: SplashViewModel
  private lateinit var binding: SplashFragmentBinding

  private val onUserCheckObserver = Observer<UserState> {
    if (it == UserState.NO_AUTH) goToWelcome()
    else if (it == UserState.AUTH) goDefender()
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
    viewModel = ViewModelProvider(this)[SplashViewModel::class.java]
    binding = SplashFragmentBinding.inflate(layoutInflater)
  }

  override fun inflateBindingVariables() {}
  override fun onBackPressed() {}

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.userIsAuthed.observe(viewLifecycleOwner, onUserCheckObserver)
    viewModel.checkUserIsAuth()
  }

  private fun goToWelcome() {
    Handler().postDelayed({
      navigate(R.id.action_splashFragment_to_welcomeFragment)
    }, 2000)
  }

  private fun goDefender() {
    Handler().postDelayed({
      navigate(R.id.action_splashFragment_to_defenderFragment)
    }, 2000)
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.userIsAuthed.removeObserver { onUserCheckObserver }
  }
}