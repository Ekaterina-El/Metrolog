package el.ka.someapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.databinding.DefenderFragmentBinding
import el.ka.someapp.databinding.FragmentLoginBinding
import el.ka.someapp.view.adapters.ErrorAdapter
import el.ka.someapp.viewmodel.LoginViewModel

class DefenderFragment: Fragment() {
  private lateinit var binding: DefenderFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    initFunctionalityParts()
    inflateBindingVariables()
    return binding.root
  }

  private fun initFunctionalityParts() {
    //viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
    binding = DefenderFragmentBinding.inflate(layoutInflater)
  }

  private fun inflateBindingVariables() {
    binding.apply {
      master = this@DefenderFragment
      lifecycleOwner = viewLifecycleOwner
    }
  }

}