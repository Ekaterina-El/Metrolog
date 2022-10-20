package el.ka.someapp.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.someapp.data.model.Node
import el.ka.someapp.databinding.ItemCompanyBinding

class NodesAdapter : RecyclerView.Adapter<NodesAdapter.ViewHolder>() {
  inner class ViewHolder(val binding: ItemCompanyBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(node: Node) {
      binding.textName.text = node.name
    }
  }

  private val items = mutableListOf<Node>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = ItemCompanyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(items[position])
  }

  override fun getItemCount(): Int = items.size

  fun setNodes(nodes: List<Node>) {
    clearList()
    nodes.forEach { addNode(it) }
  }

  private fun clearList() {
    for (i in items.size - 1 downTo 0) deleteItem(i)
  }

  private fun deleteItem(pos: Int) {
    items.remove(items[pos])
    notifyItemRemoved(pos)
  }

  private fun addNode(node: Node) {
    items.add(node)
    notifyItemInserted(items.size)
  }
}