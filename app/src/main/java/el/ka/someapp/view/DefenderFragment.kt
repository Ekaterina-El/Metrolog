package el.ka.someapp.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import el.ka.someapp.databinding.DefenderFragmentBinding
import el.ka.someapp.viewmodel.DefenderViewModel

class DefenderFragment : BaseFragment() {
  private lateinit var binding: DefenderFragmentBinding
  private lateinit var viewModel: DefenderViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    setCorrectPassword()
    return binding.root
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
    val currentPassword = sharedPreferences.getString(LOCAL_CURRENT_PASSWORD, "") ?: ""
    viewModel.setCorrectPassword(currentPassword)
  }

  companion object {
    const val LOCAL_CURRENT_PASSWORD = "local_current_password"
  }

}