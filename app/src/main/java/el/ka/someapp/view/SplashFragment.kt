package el.ka.someapp.view

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import el.ka.someapp.R

class SplashFragment : Fragment(R.layout.splash_fragment) {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Handler().postDelayed({
      goToWelcome()
    }, 3000)
  }

  private fun goToWelcome() {
    Navigation
      .findNavController(requireView())
      .navigate(R.id.action_splashFragment_to_welcomeFragment)
  }
}