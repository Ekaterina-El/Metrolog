package el.ka.someapp.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import el.ka.someapp.R
import el.ka.someapp.data.model.SpinnerItem
import el.ka.someapp.data.model.User
import el.ka.someapp.databinding.DropdownSpinnerItemBinding
import el.ka.someapp.databinding.DropdownSpinnerItemUserBinding
import el.ka.someapp.databinding.SpinnerItemBinding
import el.ka.someapp.databinding.SpinnerItemUserBinding

class SpinnerAdapter(context: Context, items: List<SpinnerItem>) :
  ArrayAdapter<SpinnerItem>(context, 0, items) {

  private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val binding: SpinnerItemBinding = if (convertView == null) {
      DataBindingUtil.inflate(
        layoutInflater,
        R.layout.spinner_item,
        parent,
        false
      )
    } else {
      convertView.tag as SpinnerItemBinding
    }

    binding.item = getItem(position)
    binding.root.tag = binding
    return binding.root
  }

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
    val binding: DropdownSpinnerItemBinding = if (convertView == null) {
      DataBindingUtil.inflate(
        layoutInflater,
        R.layout.dropdown_spinner_item,
        parent,
        false
      )
    } else {
      convertView.tag as DropdownSpinnerItemBinding
    }

    binding.root.tag = binding
    getItem(position)?.let { binding.item = it }
    return binding.root
  }
}