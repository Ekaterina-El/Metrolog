package el.ka.someapp.view.node

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import el.ka.someapp.R
import el.ka.someapp.data.model.State
import el.ka.someapp.databinding.FragmentNodeBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.NodesViewModel

class NodeFragment : BaseFragment() {
  private lateinit var binding: FragmentNodeBinding
  private val viewModel: NodesViewModel by activityViewModels()

  private val stateObserver = Observer<State> {
    when (it) {
      State.BACK -> navigateBack()
      else -> {}
    }
  }

  private fun navigateBack() {
    navigate(R.id.action_nodeFragment_to_companiesFragment)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun initFunctionalityParts() {
    binding = FragmentNodeBinding.inflate(layoutInflater)

    val navController =
      (childFragmentManager.findFragmentById(R.id.node_nav_host_fragment) as NavHostFragment).navController
    binding.bottomNavView.setupWithNavController(navController)
  }

  override fun inflateBindingVariables() {
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@NodeFragment
      viewmodel = this@NodeFragment.viewModel
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.state.observe(viewLifecycleOwner, stateObserver)
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.state.removeObserver(stateObserver)
  }

  override fun onBackPressed() {}
}