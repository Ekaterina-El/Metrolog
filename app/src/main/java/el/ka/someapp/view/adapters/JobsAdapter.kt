package el.ka.someapp.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import el.ka.someapp.R
import el.ka.someapp.data.model.JobField
import el.ka.someapp.data.model.LocalUser
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.role.AccessType
import el.ka.someapp.data.model.role.hasRole
import el.ka.someapp.databinding.ItemCompanyPositionBinding

class JobsAdapter(val context: Context, val listener: ItemListener? = null, val uid: String) :
  RecyclerView.Adapter<JobsAdapter.ViewHolder>() {
  inner class ViewHolder(val binding: ItemCompanyPositionBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private var viewerRole: UserRole? = null
    fun getViewerRole() = viewerRole

    var user: LocalUser? = null

    fun updateViewerRole(viewerRole: UserRole, localUser: LocalUser) {
      this.viewerRole = viewerRole
      this.user = localUser

      val showMenu = accessToDelete(localUser.jobField, viewerRole) || accessToEdit(
        localUser.jobField,
        viewerRole
      )
      binding.viewOptions.visibility = if (showMenu) View.VISIBLE else View.GONE
      binding.user = localUser
    }
  }

  private var viewerRole: UserRole? = null
  private val items = mutableListOf<LocalUser>()
  private val itemsHolders = mutableSetOf<ViewHolder>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding =
      ItemCompanyPositionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    val holder = ViewHolder(binding)
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val localUser = items[position]
    val role = viewerRole ?: UserRole.READER
    itemsHolders.add(holder)
    holder.updateViewerRole(role, localUser)
  }

  override fun onViewAttachedToWindow(holder: ViewHolder) {
    super.onViewAttachedToWindow(holder)
    val jobField = items[holder.adapterPosition].jobField

    holder.itemView.setOnClickListener {
      listener?.onClick(jobField)
    }

    val popupMenu = PopupMenu(context, holder.binding.viewOptions)

    popupMenu.setOnDismissListener {
      it.menu.clear()
    }

    popupMenu.setOnMenuItemClickListener {
      when (it.itemId) {
        EDIT_ITEM -> listener?.onEdit(jobField)
        DELETE_ITEM -> listener?.onDelete(jobField)
      }
      return@setOnMenuItemClickListener true
    }

    holder.binding.viewOptions.setOnClickListener {
      val role = holder.getViewerRole() ?: UserRole.READER

      val showEdit = accessToEdit(jobField, role)
      if (showEdit) popupMenu.menu.add(0, EDIT_ITEM, Menu.NONE, context.getString(R.string.edit2))

      val showDelete =
        accessToDelete(jobField, role) && (viewerRole == UserRole.HEAD || jobField.userId != uid)
      if (showDelete) popupMenu.menu.add(
        0,
        DELETE_ITEM,
        Menu.NONE,
        context.getString(R.string.delete)
      )
      popupMenu.show()
    }
  }

  private fun accessToEdit(jobField: JobField, role: UserRole): Boolean {
    return when (jobField.jobRole) {
      UserRole.HEAD -> hasRole(role, AccessType.EDIT_HEAD)
      UserRole.EDITOR_1 -> hasRole(role, AccessType.EDIT_EDITOR_1)
      UserRole.EDITOR_2 -> hasRole(role, AccessType.EDIT_EDITOR_2)
      UserRole.READER -> hasRole(role, AccessType.EDIT_READER)
    }
  }

  private fun accessToDelete(jobField: JobField, role: UserRole): Boolean {
    return when (jobField.jobRole) {
      UserRole.HEAD -> hasRole(role, AccessType.DELETE_HEAD)
      UserRole.EDITOR_1 -> hasRole(role, AccessType.DELETE_EDITOR_1)
      UserRole.EDITOR_2 -> hasRole(role, AccessType.DELETE_EDITOR_2)
      UserRole.READER -> hasRole(role, AccessType.DELETE_READER)
    }
  }

  override fun onViewDetachedFromWindow(holder: ViewHolder) {
    super.onViewDetachedFromWindow(holder)
    holder.itemView.setOnClickListener(null)
    holder.binding.viewOptions.setOnClickListener(null)
  }

  override fun getItemCount(): Int = items.size

  fun setLocalUser(localUsers: List<LocalUser>, viewerRole: UserRole? = null) {
    Log.d("JobsAdapter", "setLocalUser: ${localUsers.size}")
    clearList()
    this.viewerRole = viewerRole
    localUsers.forEach { addNode(it) }
  }

  private fun clearList() {
    itemsHolders.clear()
    for (i in items.size - 1 downTo 0) deleteItem(i)
  }

  private fun deleteItem(pos: Int) {
    items.remove(items[pos])
    notifyItemRemoved(pos)
  }

  private fun addNode(localUser: LocalUser) {
    items.add(localUser)
    notifyItemInserted(items.size)
  }

  fun updateRole(role: UserRole?) {
    val mRole = role ?: UserRole.READER
    this.viewerRole = mRole
    itemsHolders.forEach { it.updateViewerRole(mRole, it.user!!) }
//    this.viewerRole = role
  }

  companion object {
    const val EDIT_ITEM = 0
    const val DELETE_ITEM = 1
  }

  interface ItemListener {
    fun onClick(jobField: JobField)
    fun onEdit(jobField: JobField)
    fun onDelete(jobField: JobField)
  }
}