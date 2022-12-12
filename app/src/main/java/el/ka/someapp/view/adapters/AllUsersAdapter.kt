package el.ka.someapp.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import el.ka.someapp.R
import el.ka.someapp.data.model.User
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.role.AccessType
import el.ka.someapp.data.model.role.hasRole
import el.ka.someapp.databinding.ItemUserBinding

class AllUsersAdapter(val context: Context, val listener: ItemListener? = null) :
  RecyclerView.Adapter<AllUsersAdapter.ViewHolder>() {
  inner class ViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
    private var user: User? = null
    fun getUser() = user

    fun setUser(user: User) {
      binding.user = user
      this.user = user
    }

    var viewerRole: UserRole? = null
  }

  private var currentNodeLevel: Int? = null
  private var viewerRole: UserRole? = null
  private val itemsHolders = mutableListOf<ViewHolder>()
  private val items = mutableListOf<User>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    val holder =  ViewHolder(binding)
    itemsHolders.add(holder)
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val user = items[position]
    holder.setUser(user)

    val role = viewerRole ?: UserRole.READER
    holder.viewerRole = this.viewerRole

    val showMenu = accessToDelete(role, user)
    holder.binding.viewOptions.visibility = if (showMenu) View.VISIBLE else View.GONE
  }

  override fun getItemCount(): Int = items.size

  fun setUsers(users: List<User>, viewerRole: UserRole, level: Int) {
    clearList()
    this.viewerRole = viewerRole
    this.currentNodeLevel = level
    users.forEach { addUser(it) }
  }


  fun updateRole(role: UserRole?) {
    itemsHolders.forEach { it.viewerRole = role }
  }

  private fun clearList() {
    itemsHolders.clear()
    for (i in items.size - 1 downTo 0) deleteItem(i)
  }

  private fun deleteItem(pos: Int) {
    items.remove(items[pos])
    notifyItemRemoved(pos)
  }

  private fun addUser(user: User) {
    items.add(user)
    notifyItemInserted(items.size)
  }

  private var headNodeId: String = ""
  fun setHeadId(id: String) {
    this.headNodeId = id
  }

  override fun onViewAttachedToWindow(holder: ViewHolder) {
    super.onViewAttachedToWindow(holder)

    val uid = items[holder.adapterPosition].uid
    val popupMenu = PopupMenu(context, holder.binding.viewOptions)
    popupMenu.setOnDismissListener { it.menu.clear() }

    val role = holder.viewerRole ?: UserRole.READER
    val accessToDelete = accessToDelete(role, holder.getUser()!!)

    popupMenu.setOnMenuItemClickListener {
      when (it.itemId) {
        DELETE_ITEM -> if (accessToDelete) listener?.onDelete(uid)
      }
      return@setOnMenuItemClickListener true
    }

    holder.binding.viewOptions.setOnClickListener {
      if (accessToDelete) popupMenu.menu.add(
        0,
        DELETE_ITEM,
        Menu.NONE,
        context.getString(R.string.delete)
      )
      popupMenu.show()
    }
  }

  private fun accessToDelete(role: UserRole, user: User) =
    currentNodeLevel == 0
        && hasRole(role, AccessType.DELETE_USER)
        && user.uid != headNodeId

  override fun onViewDetachedFromWindow(holder: ViewHolder) {
    super.onViewDetachedFromWindow(holder)
    holder.binding.viewOptions.setOnClickListener(null)
  }

  interface ItemListener {
    fun onDelete(userId: String)
  }

  companion object {
    const val DELETE_ITEM = 0
  }
}