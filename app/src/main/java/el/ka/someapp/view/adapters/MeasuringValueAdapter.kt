package el.ka.someapp.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.someapp.data.model.measuring.MeasurementValue
import el.ka.someapp.databinding.ItemMeasuringValueBinding

class MeasuringValueAdapter(val listener: AdapterListener? = null): RecyclerView.Adapter<MeasuringValueViewHolder>() {
  private val items = mutableListOf<MeasurementValue>()
  private val holders = mutableListOf<MeasuringValueViewHolder>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasuringValueViewHolder {
    val binding = ItemMeasuringValueBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )
    val holder = MeasuringValueViewHolder(binding)
    holders.add(holder)

    return holder
  }

  override fun onBindViewHolder(holder: MeasuringValueViewHolder, position: Int) {
    holder.bind(items[position])
  }

  override fun onViewAttachedToWindow(holder: MeasuringValueViewHolder) {
    super.onViewAttachedToWindow(holder)
    holder.binding.delete.setOnClickListener {
      holders.remove(holder)
      deleteItem(holder.adapterPosition)
    }
  }

  override fun onViewDetachedFromWindow(holder: MeasuringValueViewHolder) {
    super.onViewDetachedFromWindow(holder)
    holder.binding.delete.setOnClickListener(null)
  }

  fun addNewItem() {
    items.add(0, MeasurementValue())
    notifyItemInserted(0)

    if (holders.size >= 1) {
      holders[holders.size - 1].setCanDelete(true)
    }
//    holders[0].setCanDelete(false)

    listener?.onChangeSize(items.size)
  }

  private fun deleteItem(idx: Int) {
    if (idx == 0) return
    items.removeAt(idx)
    notifyItemRemoved(idx)
    listener?.onChangeSize(items.size)
  }

  override fun getItemCount(): Int = items.size

  fun checkAllFields(): Boolean {
    var errors = 0
    holders.forEach {
      errors += if (it.hasErrors()) 1 else 0
    }
    return errors == 0
  }

  fun getMeasuringValues() = holders.map { it.getMeasuringValue() }

  fun setItems(measurementValue: List<MeasurementValue>) {
    items.clear()
    items.addAll(measurementValue)
    notifyDataSetChanged()
  }


  companion object {
    interface AdapterListener {
      fun onChangeSize(size: Int)
    }
  }
}