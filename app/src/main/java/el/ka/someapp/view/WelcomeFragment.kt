package el.ka.someapp.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import el.ka.someapp.R
import el.ka.someapp.databinding.FragmentWelcomeBinding

class WelcomeFragment: Fragment() {
  private lateinit var binding: FragmentWelcomeBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentWelcomeBinding.inflate(layoutInflater)
    binding.master = this
    return binding.root
  }

  fun toLogin() {
    Navigation
      .findNavController(requireView())
      .navigate(R.id.action_welcomeFragment_to_loginFragment)
  }

  fun toRegistration() {
    Navigation
      .findNavController(requireView())
      .navigate(R.id.action_welcomeFragment_to_registrationFragment)
  }
}