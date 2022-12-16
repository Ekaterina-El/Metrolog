package el.ka.someapp.view.measuring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import el.ka.someapp.R
import el.ka.someapp.data.model.*
import el.ka.someapp.data.model.measuring.CategoryHistory
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.data.model.measuring.MeasuringPart
import el.ka.someapp.data.model.role.AccessType
import el.ka.someapp.data.model.role.hasRole
import el.ka.someapp.databinding.FragmentMeasuringDashboardBinding
import el.ka.someapp.general.DateConvertType
import el.ka.someapp.general.convertDate
import el.ka.someapp.view.ExportFragment
import el.ka.someapp.view.adapters.MeasuringHistoryAdapter
import el.ka.someapp.view.dialog.ConfirmDialog
import el.ka.someapp.viewmodel.NodesViewModel
import el.ka.someapp.viewmodel.VisibleViewModel

class MeasuringDashboardFragment : ExportFragment() {
  private lateinit var binding: FragmentMeasuringDashboardBinding

  private val nodesViewModel: NodesViewModel by activityViewModels()
  private val visibleViewModel: VisibleViewModel by activityViewModels()

  private lateinit var measuringHistoryAdapter: MeasuringHistoryAdapter

  private val historyObserver = Observer<List<CategoryHistory>> { list ->
    val items = mutableListOf<ListItem>()
    list.forEach { category ->
      items.add(HeaderItem(category.date.convertDate(DateConvertType.TITLE)))
      category.actions.forEach {
        items.add(ContentItem(it))
      }
    }
    measuringHistoryAdapter.setItems(items)
  }

  private val stateObserver = Observer<State> {
    if (it != State.LOADING) hideLoadingDialog() else showLoadingDialog()

    when (it) {
      State.MEASURING_DELETED -> goBack()
      else -> {}
    }
  }
  private lateinit var popupMenu: PopupMenu

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  override fun initFunctionalityParts() {
    binding = FragmentMeasuringDashboardBinding.inflate(layoutInflater)

    popupMenu = PopupMenu(context, binding.verticalMenu)
    popupMenu.setOnDismissListener {
      it.menu.clear()
    }

    popupMenu.setOnMenuItemClickListener {
      when (it.itemId) {
        DELETE_ITEM -> deleteMeasuring()
        EXPORT_ITEM -> exportMeasuring()
      }
      return@setOnMenuItemClickListener true
    }
  }

  private fun exportMeasuring() {
    val measuring = nodesViewModel.currentMeasuring.value!!
    val companyName = nodesViewModel.getRootNode()!!.name
    showExportDialog(listOf(measuring), companyName)
  }

  override fun inflateBindingVariables() {
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      viewModel = this@MeasuringDashboardFragment.nodesViewModel
      master = this@MeasuringDashboardFragment
    }

    measuringHistoryAdapter = MeasuringHistoryAdapter()
    binding.historyAdapter = measuringHistoryAdapter
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    nodesViewModel.loadCurrentMeasuringHistory()
  }

  override fun onResume() {
    super.onResume()

    nodesViewModel.currentMeasuringHistory.observe(viewLifecycleOwner, historyObserver)
    nodesViewModel.state.observe(viewLifecycleOwner, stateObserver)

    val hasAccess = accessToDelete(nodesViewModel.currentRole.value!!)
    binding.verticalMenu.visibility = if (hasAccess) View.VISIBLE else View.GONE
    if (hasAccess) {
      binding.verticalMenu.setOnClickListener {
        val role = nodesViewModel.currentRole.value!!
        val showDelete = accessToDelete(role)
        if (showDelete)
          popupMenu.menu.add(0, DELETE_ITEM, Menu.NONE, requireContext().getString(R.string.delete))

        val showExportToExcel = accessToExport(role)
        if (showExportToExcel)
          popupMenu.menu.add(
            0,
            EXPORT_ITEM,
            Menu.NONE,
            requireContext().getString(R.string.export_to_excel)
          )

        popupMenu.show()
      }
    }
  }

  override fun onStop() {
    super.onStop()
    binding.verticalMenu.setOnClickListener(null)
    nodesViewModel.currentMeasuringHistory.removeObserver(historyObserver)
    nodesViewModel.state.removeObserver(stateObserver)
  }

  private fun accessToDelete(role: UserRole): Boolean = hasRole(role, AccessType.DELETE_MEASURING)
  private fun accessToExport(role: UserRole): Boolean = hasRole(role, AccessType.EXPORT_MEASURING)

  override fun onBackPressed() {
    goBack()
  }

  private fun goBack() {
    visibleViewModel.setNodeNavigationState(true)
    nodesViewModel.setCurrentMeasuring(null)
    popUp()
  }

  // region Delete measuring
  private fun deleteMeasuring() {
    val measuring = nodesViewModel.currentMeasuring.value!!
    openConfirmDialog(
      getString(R.string.delete_user_message),
      deleteMeasuringConfirmListener,
      measuring
    )
  }

  private val deleteMeasuringConfirmListener = object : ConfirmDialog.Companion.ConfirmListener {
    override fun onAgree(value: Any?) {
      nodesViewModel.deleteMeasuring(value as Measuring)
      closeConfirmDialog()
    }

    override fun onDisagree() {
      closeConfirmDialog()
    }
  }
  // endregion

  fun navigateTo(part: MeasuringPart) {
    val action = when (part) {
      MeasuringPart.PASSPORT -> R.id.action_measuringDashboardFragment_to_passportMeasuringFragment
      MeasuringPart.OVERHAUL -> R.id.action_measuringDashboardFragment_to_overhaulMeasuringFragment
      MeasuringPart.TO -> R.id.action_measuringDashboardFragment_to_TOMeasuringFragment
      MeasuringPart.MAINTENANCE_REPAIR -> R.id.action_measuringDashboardFragment_to_maintenanceRepairMeasuringFragment
      MeasuringPart.VERIFICATION -> R.id.action_measuringDashboardFragment_to_verificationMeasuringFragment
      MeasuringPart.CERTIFICATION -> R.id.action_measuringDashboardFragment_to_certificationMeasuringFragment
      MeasuringPart.CALIBRATION -> R.id.action_measuringDashboardFragment_to_calibrationMeasuringFragment
      else -> null
    }
    action?.let { navigate(action) }
  }

  companion object {
    const val DELETE_ITEM = 1
    const val EXPORT_ITEM = 2
  }
}