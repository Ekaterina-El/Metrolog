package el.ka.someapp.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.someapp.data.model.Node
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.databinding.ItemCompanyBinding
import el.ka.someapp.databinding.ItemMesuringBinding
import el.ka.someapp.generated.callback.OnClickListener

class MeasuringAdapter(val listener: ItemListener? = null) : RecyclerView.Adapter<MeasuringAdapter.ViewHolder>() {
  inner class ViewHolder(val binding: ItemMesuringBinding) : RecyclerView.ViewHolder(binding.root) {
  }

  private val items = mutableListOf<Measuring>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = ItemMesuringBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.binding.measuring = items[position]
  }

  override fun onViewAttachedToWindow(holder: ViewHolder) {
    super.onViewAttachedToWindow(holder)
    holder.itemView.setOnClickListener {
      listener?.onClick(items[holder.adapterPosition].measuringID)
    }
  }

  override fun onViewDetachedFromWindow(holder: ViewHolder) {
    super.onViewDetachedFromWindow(holder)
    holder.itemView.setOnClickListener(null)
  }

  override fun getItemCount(): Int = items.size

  fun setMeasuring(measuringItems: List<Measuring>) {
    clearList()
    measuringItems.forEach { addNode(it) }
  }

  private fun clearList() {
    for (i in items.size - 1 downTo 0) deleteItem(i)
  }

  private fun deleteItem(pos: Int) {
    items.remove(items[pos])
    notifyItemRemoved(pos)
  }

  private fun addNode(measuring: Measuring) {
    items.add(measuring)
    notifyItemInserted(items.size)
  }

  interface ItemListener {
    fun onClick(nodeId: String)
  }
}