package el.ka.someapp.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import el.ka.someapp.data.ErrorApp
import el.ka.someapp.databinding.ErrorItemBinding

class ErrorAdapter : RecyclerView.Adapter<ErrorAdapter.ViewHolder>() {
  inner class ViewHolder(private val binding: ErrorItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(error: ErrorApp) {
      binding.error = error
    }
  }

  private val items = mutableListOf<ErrorApp>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val item = ErrorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(item)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val error = items[position]
    holder.bind(error)
  }

  override fun getItemCount() = items.size

  fun setErrors(errors: List<ErrorApp>) {
    clearList()
    errors.forEach { addError(it) }
  }

  private fun clearList() {
    for (i in items.size-1 downTo 0) {
      items.remove(items[i])
      notifyItemRemoved(i)
    }
  }

  private fun addError(error: ErrorApp) {
    items.add(error)
    notifyItemInserted(items.size)
  }
}
