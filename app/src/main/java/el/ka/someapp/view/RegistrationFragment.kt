package el.ka.someapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import el.ka.someapp.data.State
import el.ka.someapp.databinding.FragmentRegistrationBinding
import el.ka.someapp.viewmodel.RegistrationViewModel

class RegistrationFragment : Fragment() {
  private lateinit var binding: FragmentRegistrationBinding
  private lateinit var viewModel: RegistrationViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    createFunctionalityParts()
    inflateBindingVariables()
    return binding.root
  }

  private fun createFunctionalityParts() {
    viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]
    binding = FragmentRegistrationBinding.inflate(layoutInflater)
  }

  private fun inflateBindingVariables() {
    binding.master = this
    binding.viewModel = viewModel
    binding.lifecycleOwner = viewLifecycleOwner
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      if (viewModel.state.value == State.LOADING) Navigation.findNavController(requireView()).popBackStack()
    }
  }

  fun registration() {
    verificationCredentials()
  }

  private fun verificationCredentials() {
    with(viewModel) {
      val s = "${email.value} ${fullName.value} ${password.value} ${repeatPassword.value}".trimIndent()
      Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()

    }
    viewModel.verificationCredentials()

  }
}