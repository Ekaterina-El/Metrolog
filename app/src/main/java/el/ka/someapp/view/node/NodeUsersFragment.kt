package el.ka.someapp.view.node

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import el.ka.someapp.R
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.User
import el.ka.someapp.databinding.FragmentNodeUsersBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.view.adapters.AllUsersAdapter
import el.ka.someapp.viewmodel.NodesViewModel

class NodeUsersFragment : BaseFragment() {
  private lateinit var binding: FragmentNodeUsersBinding
  private val viewModel: NodesViewModel by activityViewModels()

  private var addUserDialog: Dialog? = null

  private lateinit var usersAdapter: AllUsersAdapter
  private val userObserver = Observer<List<User>> {
    usersAdapter.setUsers(it)
  }

  private val stateObserver = Observer<State> {
    when(it) {
      State.ADD_USER_ERROR ->
        showAddUserDialogWithEmailAndError(
          error = getString(viewModel.addUserError.value!!.textId)
        )
      State.ADD_USER_SUCCESS -> clearAddUserDialog()
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
    viewModel.state.observe(viewLifecycleOwner, stateObserver)
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.filteredUsers.removeObserver { userObserver }
    viewModel.state.removeObserver {stateObserver }
  }

  // region Add User Dialog
  private fun createAddUserDialog() {
    addUserDialog = Dialog(requireActivity())
    addUserDialog?.let { dialog ->
      dialog.setContentView(R.layout.add_user_by_email_dialog)
      dialog.window!!.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
      )
      dialog.setCancelable(true)

      val editTextEmail: EditText = dialog.findViewById(R.id.editTextUserEmail)
      val buttonOk: Button = dialog.findViewById(R.id.buttonOk)

      buttonOk.setOnClickListener {
        val email = editTextEmail.text.toString()
        viewModel.addUserToProjectByEmail(email = email)
        dialog.dismiss()
      }
    }
  }

  fun showAddUserDialog() {
    if (addUserDialog == null) createAddUserDialog()
    addUserDialog?.show()
  }

  private fun clearAddUserDialog() {
    addUserDialog?.findViewById<EditText>(R.id.editTextUserEmail)?.setText("")
    addUserDialog?.findViewById<TextView>(R.id.textError)?.text = ""

  }

  private fun showAddUserDialogWithEmailAndError(error: String) {
    addUserDialog?.findViewById<TextView>(R.id.textError)?.text = error
    showAddUserDialog()
  }
  // endregion
}