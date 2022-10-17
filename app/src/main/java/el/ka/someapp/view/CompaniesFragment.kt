package el.ka.someapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import el.ka.someapp.databinding.FragmentCompaniesBinding

class CompaniesFragment: BaseFragment() {
  private lateinit var binding: FragmentCompaniesBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun initFunctionalityParts() {
    binding = FragmentCompaniesBinding.inflate(layoutInflater)
  }

  override fun inflateBindingVariables() {}
  override fun onBackPressed() {}
}