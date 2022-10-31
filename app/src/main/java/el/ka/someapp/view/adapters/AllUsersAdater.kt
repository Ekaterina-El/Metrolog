package el.ka.someapp.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.someapp.data.model.User
import el.ka.someapp.databinding.ItemUserBinding

class AllUsersAdapter : RecyclerView.Adapter<AllUsersAdapter.ViewHolder>() {
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
}