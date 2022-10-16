package el.ka.someapp.view

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import el.ka.someapp.R
import el.ka.someapp.viewmodel.LoginViewModel
import el.ka.someapp.viewmodel.SplashViewModel
import el.ka.someapp.viewmodel.UserState

class SplashFragment : Fragment(R.layout.splash_fragment) {
  private lateinit var viewModel: SplashViewModel

  private val onUserCheckObserver = Observer<UserState> {
    if (it == UserState.NO_AUTH) goToWelcome()
    else if (it == UserState.AUTH) goDefender()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel = ViewModelProvider(this)[SplashViewModel::class.java]
  }

  private fun goToWelcome() {
    navigate(R.id.action_splashFragment_to_welcomeFragment)
  }

  private fun goDefender() {
    navigate(R.id.action_splashFragment_to_defenderFragment)
  }

  private fun navigate(actionId: Int) {
    Navigation
      .findNavController(requireView())
      .navigate(actionId)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.userIsAuthed.observe(viewLifecycleOwner, onUserCheckObserver)
    viewModel.checkUserIsAuth()
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.userIsAuthed.removeObserver { onUserCheckObserver }
  }
}