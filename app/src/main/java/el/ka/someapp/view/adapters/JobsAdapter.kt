package el.ka.someapp.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import el.ka.someapp.R
import el.ka.someapp.data.model.LocalUser
import el.ka.someapp.databinding.ItemCompanyPositionBinding

class JobsAdapter(val context: Context, val listener: ItemListener? = null) :
  RecyclerView.Adapter<JobsAdapter.ViewHolder>() {
  inner class ViewHolder(val binding: ItemCompanyPositionBinding) :
    RecyclerView.ViewHolder(binding.root)

  private val items = mutableListOf<LocalUser>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding =
      ItemCompanyPositionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.binding.user = items[position]
  }

  override fun onViewAttachedToWindow(holder: ViewHolder) {
    super.onViewAttachedToWindow(holder)

    val uid = items[holder.adapterPosition].user.uid

    holder.itemView.setOnClickListener {
      listener?.onClick(uid)
    }

    val popupMenu = PopupMenu(context, holder.binding.viewOptions)
    popupMenu.menu.add(0, EDIT_ITEM, Menu.NONE, context.getString(R.string.edit2))
      .setIcon(R.drawable.ic_edit)
    popupMenu.menu.add(0, DELETE_ITEM, Menu.NONE, context.getString(R.string.delete))
      .setIcon(R.drawable.ic_delete)

    popupMenu.setOnMenuItemClickListener {
      when (it.itemId) {
        EDIT_ITEM -> listener?.onEdit(uid)
        DELETE_ITEM -> listener?.onEdit(uid)
      }
      return@setOnMenuItemClickListener true
    }

    holder.binding.viewOptions.setOnClickListener {
      popupMenu.show()
    }
  }


  override fun onViewDetachedFromWindow(holder: ViewHolder) {
    super.onViewDetachedFromWindow(holder)
    holder.itemView.setOnClickListener(null)
  }

  override fun getItemCount(): Int = items.size

  fun setLocalUser(localUsers: List<LocalUser>) {
    clearList()
    localUsers.forEach { addNode(it) }
  }

  private fun clearList() {
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

  companion object {
    const val EDIT_ITEM = 0
    const val DELETE_ITEM = 1
  }

  interface ItemListener {
    fun onClick(userId: String)
    fun onEdit(userId: String)
    fun onDelete(userId: String)
  }
}