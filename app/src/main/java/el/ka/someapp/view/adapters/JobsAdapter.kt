package el.ka.someapp.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.someapp.data.model.LocalUser
import el.ka.someapp.data.model.Node
import el.ka.someapp.databinding.ItemCompanyBinding
import el.ka.someapp.databinding.ItemCompanyPositionBinding
import el.ka.someapp.generated.callback.OnClickListener

class JobsAdapter(val listener: ItemListener? = null) : RecyclerView.Adapter<JobsAdapter.ViewHolder>() {
  inner class ViewHolder(val binding: ItemCompanyPositionBinding) : RecyclerView.ViewHolder(binding.root)

  private val items = mutableListOf<LocalUser>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = ItemCompanyPositionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.binding.user = items[position]
  }

  override fun onViewAttachedToWindow(holder: ViewHolder) {
    super.onViewAttachedToWindow(holder)
    holder.itemView.setOnClickListener {
      listener?.onClick(items[holder.adapterPosition].user.uid)
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

  interface ItemListener {
    fun onClick(userId: String)
  }
}