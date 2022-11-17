package el.ka.someapp.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.someapp.data.model.measuring.MeasurementValue
import el.ka.someapp.databinding.ItemMeasuringValueBinding

class MeasuringValueAdapter(val listener: AdapterListener? = null): RecyclerView.Adapter<MeasuringValueViewHolder>() {
  var items = mutableListOf<MeasurementValue>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasuringValueViewHolder {
    val binding = ItemMeasuringValueBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )
    return MeasuringValueViewHolder(binding)
  }

  override fun onBindViewHolder(holder: MeasuringValueViewHolder, position: Int) {
    holder.bind(items[position])
  }

  override fun onViewAttachedToWindow(holder: MeasuringValueViewHolder) {
    super.onViewAttachedToWindow(holder)
    holder.binding.delete.setOnClickListener { deleteItem(holder.adapterPosition) }
  }

  override fun onViewDetachedFromWindow(holder: MeasuringValueViewHolder) {
    super.onViewDetachedFromWindow(holder)
    holder.binding.delete.setOnClickListener(null)
  }

  fun addNewItem() {
    items.add(0, MeasurementValue())
    notifyItemInserted(0)
    listener?.onChangeSize(items.size)
  }


  fun deleteItem(idx: Int) {
    items.removeAt(idx)
    notifyItemRemoved(idx)
    listener?.onChangeSize(items.size)
  }

  override fun getItemCount(): Int = items.size

  companion object {
    interface AdapterListener {
      fun onChangeSize(size: Int)
    }
  }
}