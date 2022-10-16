package el.ka.someapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import el.ka.someapp.R
import el.ka.someapp.databinding.FragmentWelcomeBinding

class WelcomeFragment : BaseFragment() {
  private lateinit var binding: FragmentWelcomeBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun initFunctionalityParts() {
    binding = FragmentWelcomeBinding.inflate(layoutInflater)
  }

  override fun inflateBindingVariables() {
    binding.master = this
  }


  fun toLogin() {
    navigate(R.id.action_welcomeFragment_to_loginFragment)
  }

  fun toRegistration() {
    navigate(R.id.action_welcomeFragment_to_registrationFragment)
  }

  override fun onBackPressed() {}
}