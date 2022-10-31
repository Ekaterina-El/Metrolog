package el.ka.someapp.view.node

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import el.ka.someapp.data.model.User
import el.ka.someapp.databinding.FragmentNodeUsersBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.view.adapters.AllUsersAdapter
import el.ka.someapp.viewmodel.NodesViewModel

class NodeUsersFragment : BaseFragment() {
  private lateinit var binding: FragmentNodeUsersBinding
  private val viewModel: NodesViewModel by activityViewModels()

  private lateinit var usersAdapter: AllUsersAdapter
  private val userObserver = Observer<List<User>> {
    usersAdapter.setUsers(it)
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
    binding = FragmentNodeUsersBinding.inflate(layoutInflater)
    usersAdapter = AllUsersAdapter()
  }

  override fun inflateBindingVariables() {
    binding.apply {
      viewmodel = this@NodeUsersFragment.viewModel
      master = this@NodeUsersFragment
      lifecycleOwner = viewLifecycleOwner
      usersAdapter = this@NodeUsersFragment.usersAdapter
    }
  }

  override fun onBackPressed() {
    viewModel.goBack()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.filteredUsers.observe(viewLifecycleOwner, userObserver)
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.filteredUsers.removeObserver { userObserver }
  }
}