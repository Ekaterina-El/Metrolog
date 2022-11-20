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
import el.ka.someapp.data.model.State
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.role.AccessType
import el.ka.someapp.data.model.role.hasRole
import el.ka.someapp.databinding.FragmentMeasuringDashboardBinding
import el.ka.someapp.view.BaseFragment
import el.ka.someapp.viewmodel.NodesViewModel
import el.ka.someapp.viewmodel.VisibleViewModel

class MeasuringDashboardFragment : BaseFragment() {
  private lateinit var binding: FragmentMeasuringDashboardBinding

  private val nodesViewModel: NodesViewModel by activityViewModels()
  private val visibleViewModel: VisibleViewModel by activityViewModels()

  private val stateObserver = Observer<State> {
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
      }
      return@setOnMenuItemClickListener true
    }
  }

  override fun inflateBindingVariables() {
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      viewModel = this@MeasuringDashboardFragment.nodesViewModel
      master = this@MeasuringDashboardFragment
    }
  }

  override fun onResume() {
    super.onResume()

    nodesViewModel.state.observe(viewLifecycleOwner, stateObserver)

    val hasAccess = accessToDelete(nodesViewModel.currentRole.value!!)
    binding.verticalMenu.visibility = if (hasAccess) View.VISIBLE else View.GONE
    if (hasAccess) {
      binding.verticalMenu.setOnClickListener {
        val showDelete = accessToDelete(nodesViewModel.currentRole.value!!)
        if (showDelete)
          popupMenu.menu.add(0, DELETE_ITEM, Menu.NONE, requireContext().getString(R.string.delete))
        popupMenu.show()
      }
    }
  }

  override fun onStop() {
    super.onStop()
    binding.verticalMenu.setOnClickListener(null)

    nodesViewModel.state.removeObserver(stateObserver)
  }

  private fun accessToDelete(role: UserRole): Boolean = hasRole(role, AccessType.DELETE_MEASURING)

  override fun onBackPressed() {
    goBack()
  }

  private fun goBack() {
    visibleViewModel.setNodeNavigationState(true)
    nodesViewModel.setCurrentMeasuring(null)
    popUp()
  }

  private fun deleteMeasuring() {
    nodesViewModel.deleteMeasuring(nodesViewModel.currentMeasuring.value!!)
  }

  companion object {
    const val DELETE_ITEM = 1
  }
}