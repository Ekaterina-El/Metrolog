package el.ka.someapp.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.someapp.data.model.ListItem
import el.ka.someapp.data.model.TypeListItem
import el.ka.someapp.data.model.measuring.MeasuringHistoryItemExecuted
import el.ka.someapp.databinding.ItemHistoryBinding
import el.ka.someapp.databinding.ItemHistoryHeaderBinding

class MeasuringHistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  private val list: MutableList<ListItem> = mutableListOf()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      TypeListItem.HEADER.idx -> {
        val binding =
          ItemHistoryHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        MeasuringHistoryHeaderViewHolder(binding)
      }

      else -> {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        MeasuringHistoryContentViewHolder(binding)
      }
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = list[position]
    when (item.type) {
      TypeListItem.HEADER -> {
        val value = item.value as String
        (holder as MeasuringHistoryHeaderViewHolder).bind(value)
      }

      TypeListItem.CONTENT -> {
        val value = item.value as MeasuringHistoryItemExecuted
        (holder as MeasuringHistoryContentViewHolder).bind(value)
      }
    }
  }

  override fun getItemCount() = list.size

  override fun getItemViewType(position: Int) = list[position].type.idx

  @SuppressLint("NotifyDataSetChanged")
  fun setItems(newList: List<ListItem>) {
    list.clear()
    list.addAll(newList)
    notifyDataSetChanged()
  }
}

class MeasuringHistoryContentViewHolder(private val binding: ItemHistoryBinding) :
  RecyclerView.ViewHolder(binding.root) {
  fun bind(value: MeasuringHistoryItemExecuted) {
    binding.history = value
  }
}

class MeasuringHistoryHeaderViewHolder(private val binding: ItemHistoryHeaderBinding) :
  RecyclerView.ViewHolder(binding.root) {
  fun bind(title: String) {
    binding.title = title
  }
}