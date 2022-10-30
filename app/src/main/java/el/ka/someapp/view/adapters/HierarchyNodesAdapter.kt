package el.ka.someapp.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.someapp.data.model.Node
import el.ka.someapp.databinding.ItemHierarchyBinding

class HierarchyNodesAdapter(val listener: ItemListener? = null) :
  RecyclerView.Adapter<HierarchyNodesAdapter.ViewHolder>() {
  inner class ViewHolder(val binding: ItemHierarchyBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(node: Node) {
      binding.textViewNodeName.text = node.name
    }
  }

  private val items = mutableListOf<Node>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = ItemHierarchyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(items[position])
  }

  override fun onViewAttachedToWindow(holder: ViewHolder) {
    super.onViewAttachedToWindow(holder)
    val pos = holder.adapterPosition

    val isLast = items.size - 1 == pos
    holder.binding.isCurrent = isLast
    holder.binding.isLast = isLast
    holder.binding.isFirst = pos == 0

    holder.itemView.setOnClickListener {
      listener?.onClick(items[pos])
    }
  }

  override fun onViewDetachedFromWindow(holder: ViewHolder) {
    super.onViewDetachedFromWindow(holder)
    holder.itemView.setOnClickListener(null)
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

  interface ItemListener {
    fun onClick(node: Node)
  }
}