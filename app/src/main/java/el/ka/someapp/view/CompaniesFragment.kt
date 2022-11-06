package el.ka.someapp.view

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import el.ka.someapp.R
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.Node
import el.ka.someapp.data.model.State
import el.ka.someapp.databinding.ChangeUserDialogBinding
import el.ka.someapp.databinding.FragmentCompaniesBinding
import el.ka.someapp.view.adapters.NodesAdapter
import el.ka.someapp.viewmodel.ChangeUserFullNameViewModel
import el.ka.someapp.viewmodel.NodesViewModel


class CompaniesFragment : BaseFragment() {
  private lateinit var binding: FragmentCompaniesBinding
  private lateinit var adapter: NodesAdapter
  private val viewModel: NodesViewModel by activityViewModels()
  private val nodesObserver = Observer<List<Node>> {
    adapter.setNodes(it)
  }
  private val stateObserver = Observer<State> {
    when (it) {
      State.NON_UNIQUE_NAME -> {
        showCreateDialogWithError(getString(Errors.nonUniqueName.textId))
      }
      State.NEW_NODE_ADDED -> {
        clearDialog()
        viewModel.toViewState()
      }
      else -> {}
    }
  }

  private lateinit var dialog: Dialog

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun initFunctionalityParts() {
    viewModel.loadNodeByID(null)

    binding = FragmentCompaniesBinding.inflate(layoutInflater)
    adapter = NodesAdapter(
      listener = object : NodesAdapter.ItemListener {
        override fun onClick(nodeId: String) {
          openNode(nodeId)
        }
      }
    )
    createAddCompanyDialog()
  }

  override fun inflateBindingVariables() {
    binding.listCompanies.itemAnimator = null

    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      adapter = this@CompaniesFragment.adapter
      viewModel = this@CompaniesFragment.viewModel
      master = this@CompaniesFragment
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.filteredNodes.observe(viewLifecycleOwner, nodesObserver)
    viewModel.state.observe(viewLifecycleOwner, stateObserver)
    viewModel.loadNodes()
  }

  override fun onDestroyView() {
    changeUserFullNameViewModel?.state?.removeObserver(changeUserObserverState)
    viewModel.filteredNodes.removeObserver(nodesObserver)
    viewModel.state.removeObserver(stateObserver)
    super.onDestroyView()
  }

  override fun onBackPressed() {}

  fun changeProfileImage() {

    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
  }

  private val cropImage = registerForActivityResult(CropImageContract()) { result ->
    if (result.isSuccessful) {
      val uriContent = result.uriContent!!
      binding.imageViewProfile.setImageURI(uriContent)
      viewModel.changeProfileImage(uri = uriContent)
    } else {
      val exception = result.error
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == PICK_IMAGE_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        val image: Uri = data!!.data!!
        cropImage.launch(
          CropImageContractOptions(
            uri = image,
            CropImageOptions(
              guidelines = CropImageView.Guidelines.ON,
              aspectRatioX = 1,
              aspectRatioY = 1,
              fixAspectRatio = true,
            )
          )
        )
      }
    }
  }


  private fun openNode(nodeId: String) {
    viewModel.loadNodeByID(nodeId)
    navigate(R.id.action_companiesFragment_to_nodeFragment)
  }

  fun logout() {
    // Show Dialog -> Are you sure?
    viewModel.logout {
      navigate(R.id.action_companiesFragment_to_welcomeFragment)
    }
  }

  // region Add Company Dialog
  private fun createAddCompanyDialog() {
    dialog = Dialog(requireActivity())
    dialog.setContentView(R.layout.add_node_dialog)
    dialog.window!!.setLayout(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )
    dialog.setCancelable(true)

    val editTextNodeName: EditText = dialog.findViewById(R.id.editTextNodeName)
    val buttonOk: Button = dialog.findViewById(R.id.buttonOk)

    buttonOk.setOnClickListener {
      val value = editTextNodeName.text.toString()
      if (value.isNotEmpty()) {
        viewModel.addNodeWithName(value)
      } else {
        clearDialog()
      }
      dialog.dismiss()
    }
  }

  private fun showCreateDialogWithError(error: String) {
    dialog.findViewById<TextView>(R.id.textError).text = error
    showAddCompanyDialog()
  }

  fun showAddCompanyDialog() {
    dialog.show()
  }

  private fun clearDialog() {
    dialog.findViewById<EditText>(R.id.editTextNodeName).setText("")
    dialog.findViewById<TextView>(R.id.textError).text = null
  }


  override fun onDestroy() {
    super.onDestroy()
    viewModel.filteredNodes.removeObserver { nodesObserver }
    viewModel.state.removeObserver { stateObserver }
  }
  // endregion

  // region Change user full name dialog
  private var changeUserFullNameDialog: Dialog? = null
  private var changeUserFullNameViewModel: ChangeUserFullNameViewModel? = null
  private lateinit var bindingChangeUserFullNameDialog: ChangeUserDialogBinding

  private val changeUserObserverState = Observer<State> {
    when (it) {
      State.FULL_NAME_CHANGED -> {
        changeUserFullNameDialog!!.dismiss()
        viewModel.loadCurrentUserProfile()
      }
      else -> {}
    }
  }

  private fun createChangeUserFullNameDialog() {
    changeUserFullNameDialog = Dialog(requireContext())

    changeUserFullNameViewModel = ViewModelProvider(this)[ChangeUserFullNameViewModel::class.java]
    changeUserFullNameViewModel?.state?.observe(viewLifecycleOwner, changeUserObserverState)

    changeUserFullNameDialog?.let { dialog ->
      bindingChangeUserFullNameDialog =
        ChangeUserDialogBinding.inflate(LayoutInflater.from(requireContext()))
      dialog.setContentView(bindingChangeUserFullNameDialog.root)


      bindingChangeUserFullNameDialog.viewModel = changeUserFullNameViewModel
      bindingChangeUserFullNameDialog.lifecycleOwner = viewLifecycleOwner

      dialog.window!!.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
      )
      dialog.setCancelable(true)
    }
  }

  fun showCreateChangeUserFullNameDialog() {
    if (changeUserFullNameDialog == null) createChangeUserFullNameDialog()
    changeUserFullNameViewModel!!.newFullName.value = viewModel.currentUserProfile.value!!.fullName
    changeUserFullNameDialog!!.show()
  }
  // endregion

  companion object {
    const val PICK_IMAGE_REQUEST_CODE = 1001
  }
}