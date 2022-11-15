package el.ka.someapp.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import el.ka.someapp.R
import el.ka.someapp.data.model.User
import el.ka.someapp.databinding.ItemUserBinding

class AllUsersAdapter(val context: Context, val listener: ItemListener? = null) :
  RecyclerView.Adapter<AllUsersAdapter.ViewHolder>() {
  inner class ViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

  private val items = mutableListOf<User>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(binding = binding)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.binding.user = items[position]
  }

  override fun getItemCount(): Int = items.size

  fun setUsers(users: List<User>) {
    clearList()
    users.forEach { addUser(it) }
  }

  private fun clearList() {
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

  override fun onViewAttachedToWindow(holder: ViewHolder) {
    super.onViewAttachedToWindow(holder)

    val uid = items[holder.adapterPosition].uid

    val popupMenu = PopupMenu(context, holder.binding.viewOptions)
    popupMenu.menu.add(0, DELETE_ITEM, Menu.NONE, context.getString(R.string.delete))

    popupMenu.setOnMenuItemClickListener {
      when (it.itemId) {
        DELETE_ITEM -> listener?.onDelete(uid)
      }
      return@setOnMenuItemClickListener true
    }

    holder.binding.viewOptions.setOnClickListener {
      popupMenu.show()
    }
  }

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