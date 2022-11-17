package el.ka.someapp.view.adapters

import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import el.ka.someapp.R
import el.ka.someapp.data.model.measuring.MeasurementValue
import el.ka.someapp.databinding.ItemMeasuringValueBinding
import el.ka.someapp.view.BaseFragment

class MeasuringValueViewHolder(val binding: ItemMeasuringValueBinding) :
  RecyclerView.ViewHolder(binding.root) {
  fun bind(measurementValue: MeasurementValue) {
    binding.inpName.setText(measurementValue.name)
    binding.inpRange.setText(measurementValue.range)
    binding.inpUnits.setText(measurementValue.units)
    binding.inpGraduationPoint.setText(measurementValue.graduationPoint)
    binding.inpAccuracyClass.setText(measurementValue.accuracyClass)
  }

  fun checkFields(): Boolean {
    var errors = 0
    val isRequireString = binding.root.context.getString(R.string.is_required)

    // Наиминование: обязательно
    var layout = binding.layoutName
    var value = binding.inpName.text.toString()
    if (BaseFragment.checkIsNoEmpty(layout, value, isRequireString)) errors += 1

    // Предел: обязательно
    layout = binding.layoutRange
    value = binding.inpRange.text.toString()
    if (BaseFragment.checkIsNoEmpty(layout, value, isRequireString)) errors += 1

    // Единицы измерения: обязательно
    layout = binding.layoutUnits
    value = binding.inpUnits.text.toString()
    if (BaseFragment.checkIsNoEmpty(layout, value, isRequireString)) errors += 1

    return errors == 0
  }

  fun addListeners() {
    binding.inpName.doAfterTextChanged {

    }
  }
}