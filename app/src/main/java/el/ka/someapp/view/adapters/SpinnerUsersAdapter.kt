package el.ka.someapp.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import el.ka.someapp.R
import el.ka.someapp.data.model.User
import el.ka.someapp.databinding.DropdownSpinnerItemUserBinding
import el.ka.someapp.databinding.SpinnerItemUserBinding

class SpinnerUsersAdapter(context: Context, users: List<User>) :
  ArrayAdapter<User>(context, 0, users) {
  private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

    val binding: SpinnerItemUserBinding = if (convertView == null) {
      DataBindingUtil.inflate(
        layoutInflater,
        R.layout.spinner_item_user,
        parent,
        false
      )
    } else {
      convertView.tag as SpinnerItemUserBinding
    }

    binding.user = getItem(position)
    binding.root.tag = binding
    return binding.root
  }

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
    val binding: DropdownSpinnerItemUserBinding = if (convertView == null) {
      DataBindingUtil.inflate(
        layoutInflater,
        R.layout.dropdown_spinner_item_user,
        parent,
        false
      )
    } else {
      convertView.tag as DropdownSpinnerItemUserBinding
    }

    binding.root.tag = binding
    getItem(position)?.let { binding.user = it }

    return binding.root
  }
}