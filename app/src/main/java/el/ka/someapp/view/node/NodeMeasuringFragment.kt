package el.ka.someapp.view.node

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import el.ka.someapp.MainActivity
import el.ka.someapp.R
import el.ka.someapp.data.model.ExportType
import el.ka.someapp.data.model.Node
import el.ka.someapp.data.model.measuring.LoadMeasuringState
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.data.model.role.AccessType
import el.ka.someapp.data.model.role.hasRole
import el.ka.someapp.databinding.FragmentNodeMeasuringBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.view.adapters.MeasuringAdapter
import el.ka.someapp.view.dialog.ExportDialog
import el.ka.someapp.viewmodel.NodesViewModel
import el.ka.someapp.viewmodel.VisibleViewModel

class NodeMeasuringFragment : BaseFragment() {
  private lateinit var binding: FragmentNodeMeasuringBinding
  private val viewModel: NodesViewModel by activityViewModels()
  private val visibleViewModel: VisibleViewModel by activityViewModels()

  private lateinit var measuringAdapter: MeasuringAdapter

  private val measuringObserver = Observer<List<Measuring>> {
    measuringAdapter.setMeasuring(it)
  }

  private val currentNodeObserver = Observer<Node?> {
    viewModel.loadMeasuringByState()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel.loadMeasuringByState()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return binding.root
  }

  private val measuringListener = object : MeasuringAdapter.ItemListener {
    override fun onClick(measuring: Measuring) {
      if (hasRole(viewModel.currentRole.value!!, AccessType.VIEW_MEASURING)) {
        visibleViewModel.setNodeNavigationState(false)
        viewModel.setCurrentMeasuring(measuring)
        navigate(R.id.action_nodeMeasuringFragment_to_measuringDashboardFragment)
      }
    }
  }

  override fun initFunctionalityParts() {
    binding = FragmentNodeMeasuringBinding.inflate(layoutInflater)

    measuringAdapter = MeasuringAdapter(measuringListener)
    val d = DividerItemDecoration(binding.listMeasuring.context, DividerItemDecoration.VERTICAL)
    binding.listMeasuring.addItemDecoration(d)
  }

  override fun inflateBindingVariables() {
    binding.apply {
      viewmodel = this@NodeMeasuringFragment.viewModel
      master = this@NodeMeasuringFragment
      lifecycleOwner = viewLifecycleOwner
      measuringAdapter = this@NodeMeasuringFragment.measuringAdapter
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.measuringFiltered.observe(viewLifecycleOwner, measuringObserver)
    viewModel.currentNode.observe(viewLifecycleOwner, currentNodeObserver)

    binding.switchLoadMeasuringState.setOnCheckedChangeListener { _, isEnable ->
      val loadAll = when (isEnable) {
        true -> LoadMeasuringState.ALL
        false -> LoadMeasuringState.CURRENT
      }
      viewModel.setLoadMeasuringState(loadAll)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.measuringFiltered.removeObserver(measuringObserver)
    viewModel.currentNode.removeObserver(currentNodeObserver)
    binding.switchLoadMeasuringState.setOnCheckedChangeListener(null)
  }

  override fun onBackPressed() {
    viewModel.goBack()
  }

  fun goAddMeasuring() {
    visibleViewModel.setNodeNavigationState(false)
    navigate(R.id.action_nodeMeasuringFragment_to_addMeasuringFragment)
  }

  fun exportMeasuringItems() {
    val size = viewModel.measuringFiltered.value!!.size
    if (size == 0) nothingToExport() else showExportDialog()
  }

  private var exportDialog: ExportDialog? = null
  private val exportDialogListener = object : ExportDialog.Companion.DialogListener {
    override fun onContinue(exportTypes: List<ExportType>) {
      if (exportTypes.isEmpty()) noCheckedExportTypes() else startExport(exportTypes)
    }
  }

  private fun startExport(exportTypes: List<ExportType>) {
    exportDialog!!.closeConfirmDialog()
    export(exportTypes)
  }

  private fun export(exportTypes: List<ExportType>) {
    val measuring = viewModel.measuringFiltered.value!!
    val companyName = viewModel.getRootNode()?.name ?: "-"

    ((context) as MainActivity).exporter.export(exportTypes, measuring, companyName) {
//        openExcelDocument(path)
      toast(R.string.you_measuring_exported_successfully)
    }
  }

  private fun nothingToExport() {
    toast(R.string.nothingToExport)
  }

  private fun noCheckedExportTypes() {
    exportDialog!!.closeConfirmDialog()
    toast(R.string.noCheckedExportTypes)
  }

  private fun showExportDialog() {
    if (exportDialog == null) exportDialog = ExportDialog.getInstance(requireContext())
    val measuringItems = viewModel.measuringFiltered.value!!.map { it.passport.name }
    exportDialog!!.openConfirmDialog(exportDialogListener, measuringItems)
  }
}