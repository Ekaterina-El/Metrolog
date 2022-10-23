package el.ka.someapp.view.node

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import el.ka.someapp.R
import el.ka.someapp.databinding.FragmentNodeBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.NodesViewModel

class NodeFragment : BaseFragment() {
  private lateinit var binding: FragmentNodeBinding
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
    binding = FragmentNodeBinding.inflate(layoutInflater)

    val navController = (childFragmentManager.findFragmentById(R.id.node_nav_host_fragment) as NavHostFragment).navController
    binding.bottomNavView.setupWithNavController(navController)

//    binding.bottomNavView.setupWithNavController(binding.nodeNavHostFragment.findNavController())
/*
    val navController = (binding.nodeNavHostFragment.findNavController())
    val navView: BottomNavigationView = binding.bottomNavView
    navView.setupWithNavController(navController)

 */
  }

  override fun inflateBindingVariables() {
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@NodeFragment
      viewmodel = this@NodeFragment.viewModel
    }
  }

  override fun onBackPressed() {
    navigate(R.id.action_nodeFragment_to_companiesFragment)
  }
}