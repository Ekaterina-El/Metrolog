package el.ka.someapp.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import el.ka.someapp.R
import el.ka.someapp.data.model.*
import el.ka.someapp.databinding.FragmentCompaniesBinding
import el.ka.someapp.view.adapters.NodesAdapter
import el.ka.someapp.view.dialog.AddCompanyDialog
import el.ka.someapp.view.dialog.ChangeUserFullNameDialog
import el.ka.someapp.view.dialog.ConfirmDialog
import el.ka.someapp.viewmodel.ChangeImageViewModel
import el.ka.someapp.viewmodel.ChangeUserFullNameViewModel
import el.ka.someapp.viewmodel.NodesViewModel


class CompaniesFragment : BaseFragment() {
  private lateinit var binding: FragmentCompaniesBinding
  private lateinit var adapter: NodesAdapter

  private lateinit var changeImageViewModel: ChangeImageViewModel

  private val viewModel: NodesViewModel by activityViewModels()
  private val nodesObserver = Observer<List<Node>> {
    adapter.setNodes(it)
  }

  private val stateObserver = Observer<State> {
//    if (it != State.LOADING) hideLoadingDialog()

    when (it) {
      State.NON_UNIQUE_NAME -> {
        showCreateDialogWithError(getString(Errors.nonUniqueName.textId))
      }
      State.NEW_NODE_ADDED -> {
        clearDialog()
        viewModel.toViewState()
      }
//      State.LOADING -> showLoadingDialog()
      else -> {}
    }
  }

  private val loadsObserver = Observer<MutableSet<Int>> {
    if (it.isNotEmpty()) showLoadingDialog() else hideLoadingDialog()
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
    viewModel.loadNodeByID(null)

    binding = FragmentCompaniesBinding.inflate(layoutInflater)
    changeImageViewModel = ViewModelProvider(this)[ChangeImageViewModel::class.java]
    adapter = NodesAdapter(
      listener = object : NodesAdapter.ItemListener {
        override fun onClick(nodeId: String) {
          openNode(nodeId)
        }
      }
    )

    val d = DividerItemDecoration(binding.listCompanies.context, DividerItemDecoration.VERTICAL)
    binding.listCompanies.addItemDecoration(d)
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

  override fun onResume() {
    super.onResume()
    viewModel.filteredNodes.observe(viewLifecycleOwner, nodesObserver)
    viewModel.state.observe(viewLifecycleOwner, stateObserver)
    viewModel.loads.observe(viewLifecycleOwner, loadsObserver)
    viewModel.loadNodes()
  }

  override fun onStop() {
    super.onStop()
    changeUserFullNameViewModel?.state?.removeObserver(changeUserObserverState)
    viewModel.filteredNodes.removeObserver(nodesObserver)
    viewModel.state.removeObserver(stateObserver)
    viewModel.loads.removeObserver(loadsObserver)
  }

  override fun onBackPressed() {}

  private fun openNode(nodeId: String) {
    val action = CompaniesFragmentDirections.actionCompaniesFragmentToNodeFragment(nodeId)
    navigate(action)
  }

  // region Change Image (Profile & BG)
  fun changeImage(imageType: ImageType) {
    changeImageViewModel.setCurrentChangeImageType(imageType)

    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
  }

  private val cropImage = registerForActivityResult(CropImageContract()) { result ->
    if (!result.isSuccessful) return@registerForActivityResult

    val uriContent = result.uriContent!!
    when (changeImageViewModel.currentChangeImageType.value) {
      ImageType.PROFILE -> setProfile(uriContent)
      ImageType.BACKGROUND -> setBackground(uriContent)
      else -> {}
    }
  }

  private fun setProfile(uriContent: Uri) {
    binding.imageViewProfile.setImageURI(uriContent)
    viewModel.changeProfileImage(uri = uriContent)
  }

  private fun setBackground(uri: Uri) {
    binding.profileBackground.setImageURI(uri)
    viewModel.changeBackgroundImage(uri = uri)
  }

  @Deprecated("Deprecated in Java")
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      cropImage.launch(
        CropImageContractOptions(
          uri = data!!.data!!,
          CropImageOptions(
            guidelines = CropImageView.Guidelines.ON,
            aspectRatioX = changeImageViewModel.aspectRationX.value!!,
            aspectRatioY = changeImageViewModel.aspectRationY.value!!,
            fixAspectRatio = true,
          )
        )
      )
    }
  }
  // endregion

  // region Logout Dialog
  private val logoutConfirmListener = object : ConfirmDialog.Companion.ConfirmListener {
    override fun onAgree(value: Any?) {
      viewModel.logout { afterLogout() }
    }

    override fun onDisagree() {
      closeConfirmDialog()
    }
  }

  private fun showLogoutDialog() {
    openConfirmDialog(getString(R.string.exit_message), logoutConfirmListener)
  }

  private fun afterLogout() {
    closeConfirmDialog()
    setPassword(null)
    navigate(R.id.action_companiesFragment_to_welcomeFragment)
  }

  fun logout() {
    showLogoutDialog()
  }
  // endregion

  // region Add Company Dialog
  private var addCompanyDialog: AddCompanyDialog? = null
  private var addCompanyListener = object : AddCompanyDialog.Companion.Listener {
    override fun saveNode(value: String) {
      viewModel.addNodeWithName(value)
    }
  }

  fun showAddCompanyDialog() {
    if (addCompanyDialog == null) addCompanyDialog =
      AddCompanyDialog.getInstance(requireContext(), addCompanyListener)

    addCompanyDialog!!.setListener(addCompanyListener)
    addCompanyDialog!!.showDialog()
  }

  private fun showCreateDialogWithError(error: String) {
    if (addCompanyDialog == null) addCompanyDialog =
      AddCompanyDialog.getInstance(requireContext(), addCompanyListener)

    addCompanyDialog!!.setListener(addCompanyListener)
    addCompanyDialog!!.showDialog(error, showError = true)
  }

  private fun clearDialog() {
    addCompanyDialog?.clearDialog()
  }
  // endregion

  // region Change user full name dialog
  private var changeUserFullNameDialog: ChangeUserFullNameDialog? = null
  private var changeUserFullNameViewModel: ChangeUserFullNameViewModel? = null

  private val changeUserObserverState = Observer<State> {
    when (it) {
      State.FULL_NAME_CHANGED -> {
        changeUserFullNameDialog!!.dismissDialog()
        viewModel.loadCurrentUserProfile()
      }
      else -> {}
    }
  }

  private val changeUserObserverError = Observer<ErrorApp?> {
    changeUserFullNameDialog?.showDialogWithError(getString(it.textId))
  }

  fun showCreateChangeUserFullNameDialog() {
    if (changeUserFullNameDialog == null) {
      changeUserFullNameViewModel =
        ViewModelProvider(this)[ChangeUserFullNameViewModel::class.java]
      changeUserFullNameViewModel?.state?.observe(viewLifecycleOwner, changeUserObserverState)
      changeUserFullNameViewModel?.error?.observe(viewLifecycleOwner, changeUserObserverError)
    }
    changeUserFullNameViewModel!!.newFullName.value =
      viewModel.currentUserProfile.value!!.fullName

    if (changeUserFullNameDialog == null) {
      changeUserFullNameDialog = ChangeUserFullNameDialog.getInstance(
        requireContext(),
        changeUserFullNameViewModel!!, viewLifecycleOwner
      )
    }

    changeUserFullNameDialog!!.clearDialog()
    changeUserFullNameDialog!!.showDialog()
  }
  // endregion

  companion object {
    const val PICK_IMAGE_REQUEST_CODE = 1001
  }
}