package el.ka.someapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import el.ka.someapp.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {
  private lateinit var binding: FragmentRegistrationBinding
  private var isLoading = false

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentRegistrationBinding.inflate(layoutInflater)
    binding.master = this
    return binding.root
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      if (!isLoading) Navigation.findNavController(requireView()).popBackStack()
    }
  }

  fun registration() {
    isLoading = true
    binding.layoutLoader.visibility = View.VISIBLE
  }
}