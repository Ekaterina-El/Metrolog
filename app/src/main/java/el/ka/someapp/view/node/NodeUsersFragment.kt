package el.ka.someapp.view.node

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import el.ka.someapp.databinding.FragmentNodeUsersBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.NodesViewModel

class NodeUsersFragment : BaseFragment() {
  private lateinit var binding: FragmentNodeUsersBinding
  private val viewModel: NodesViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun initFunctionalityParts() {
    binding = FragmentNodeUsersBinding.inflate(layoutInflater)
  }

  override fun inflateBindingVariables() {
    binding.apply {
      viewmodel = this@NodeUsersFragment.viewModel
      master = this@NodeUsersFragment
      lifecycleOwner = viewLifecycleOwner
    }
  }

  override fun onBackPressed() {
  }
}