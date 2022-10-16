package el.ka.someapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import el.ka.someapp.databinding.DefenderFragmentBinding

class DefenderFragment : BaseFragment() {
  private lateinit var binding: DefenderFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun initFunctionalityParts() {
    binding = DefenderFragmentBinding.inflate(layoutInflater)
  }

  override fun inflateBindingVariables() {
    binding.apply {
      master = this@DefenderFragment
      lifecycleOwner = viewLifecycleOwner
    }
  }

  override fun onBackPressed() {}

}