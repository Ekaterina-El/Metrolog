package el.ka.someapp.view.node

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import el.ka.someapp.R
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.User
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.databinding.FragmentNodeUsersBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.view.adapters.AllUsersAdapter
import el.ka.someapp.view.dialog.AddUserDialog
import el.ka.someapp.view.dialog.ConfirmDialog
import el.ka.someapp.viewmodel.NodesViewModel

class NodeUsersFragment : BaseFragment() {
  private lateinit var binding: FragmentNodeUsersBinding
  private val viewModel: NodesViewModel by activityViewModels()

  private val roleObserver = Observer<UserRole?> {
    if (viewModel.currentNode.value!!.level == 0) usersAdapter.setHeadId(
      viewModel.getLocalUserHeadID() ?: ""
    )
    usersAdapter.updateRole(it)
  }

  private lateinit var usersAdapter: AllUsersAdapter
  private val userObserver = Observer<List<User>> {
    usersAdapter.setUsers(it, viewModel.currentRole.value ?: UserRole.READER, viewModel.currentNode.value?.level ?: 0)
  }
  private val usersListener = object : AllUsersAdapter.ItemListener {
    override fun onDelete(userId: String) {
      showDeleteUserConfirm(userId)
    }
  }

  // region Delete User
  private val deleteUserConfirmListener = object : ConfirmDialog.Companion.ConfirmListener {
    override fun onAgree(value: Any?) {
      viewModel.denyAccessUser(value as String)
      closeConfirmDialog()
    }

    override fun onDisagree() {
      closeConfirmDialog()
    }
  }

  private fun showDeleteUserConfirm(userId: String) {
    openConfirmDialog(
      getString(R.string.delete_user_message),
      deleteUserConfirmListener,
      userId
    )
  }
  // endregion

  private val stateObserver = Observer<State> {
    when (it) {
      State.ADD_USER_ERROR ->
        showAddUserDialogWithEmailAndError(error = getString(viewModel.addUserError.value!!.textId))
      State.ADD_USER_SUCCESS -> addUserDialog!!.clearDialog()
      else -> {}
    }
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
    usersAdapter = AllUsersAdapter(
      context = requireContext(),
      listener = usersListener
    )

    val d = DividerItemDecoration(binding.listUsers.context, DividerItemDecoration.VERTICAL)
    binding.listUsers.addItemDecoration(d)
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

  override fun onResume() {
    super.onResume()
    viewModel.filteredUsers.observe(viewLifecycleOwner, userObserver)
    viewModel.state.observe(viewLifecycleOwner, stateObserver)
    viewModel.currentRole.observe(viewLifecycleOwner, roleObserver)
  }

  override fun onStop() {
    super.onStop()
    viewModel.filteredUsers.removeObserver { userObserver }
    viewModel.state.removeObserver { stateObserver }
    viewModel.currentRole.removeObserver { roleObserver }
  }


  // region Add User Dialog
  private var addUserDialog: AddUserDialog? = null
  private val addUserDialogListener = object : AddUserDialog.Companion.Listener {
    override fun addUser(email: String) {
      viewModel.addUserToProjectByEmail(email)
      addUserDialog!!.dismiss()
    }
  }

  private fun showAddUserDialogWithEmailAndError(error: String) {
    if (addUserDialog == null) addUserDialog = AddUserDialog(requireContext())
    addUserDialog!!.setListener(addUserDialogListener)
    addUserDialog!!.show(error)
  }

  fun showAddUserDialog() {
    if (addUserDialog == null) addUserDialog = AddUserDialog(requireContext())
    addUserDialog!!.setListener(addUserDialogListener)
    addUserDialog!!.show()
  }

  // region Exit project dialog
  private val exitProjectConfirmListener = object : ConfirmDialog.Companion.ConfirmListener {
    override fun onAgree(value: Any?) {
      viewModel.exitFromProject { afterExitFromProject() }
      closeConfirmDialog()
    }

    override fun onDisagree() {
      closeConfirmDialog()
    }
  }

  private fun afterExitFromProject() {
    goCompaniesScreen()
  }

  private fun goCompaniesScreen() {
    viewModel.exitCompany()
  }

  fun showConfirmProjectExitDialog() {
    openConfirmDialog(getString(R.string.exit_from_project_message), exitProjectConfirmListener)
  }
  // endregion
}