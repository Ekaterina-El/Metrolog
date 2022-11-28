package el.ka.someapp.view.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import el.ka.someapp.R
import el.ka.someapp.data.model.measuring.MeasurementValue
import el.ka.someapp.databinding.ItemMeasuringValueBinding
import el.ka.someapp.view.BaseFragment

class MeasuringValueViewHolder(val binding: ItemMeasuringValueBinding) :
  RecyclerView.ViewHolder(binding.root) {
  fun bind(measurementValue: MeasurementValue) {
    binding.inpName.setText(measurementValue.name)
    binding.inpRangeMin.setText(measurementValue.rangeMin)
    binding.inpRangeMax.setText(measurementValue.rangeMax)
    binding.inpUnits.setText(measurementValue.units)
    binding.inpGraduationPoint.setText(measurementValue.graduationPoint)
    binding.inpAccuracyClass.setText(measurementValue.accuracyClass)

    binding.delete.visibility = View.GONE
  }

  fun setCanDelete(state: Boolean) {
    binding.delete.visibility = if (state) View.VISIBLE else View.GONE
  }

  fun hasErrors(): Boolean {
    var errors = 0
    val isRequireString = binding.root.context.getString(R.string.is_required)

    // Наиминование: обязательно
    var layout = binding.layoutName
    var value = binding.inpName.text.toString()
    if (BaseFragment.checkIsNoEmpty(layout, value, isRequireString)) errors += 1

    // Нижний предел (от): обязательно
    layout = binding.layoutRangeMin
    value = binding.inpRangeMin.text.toString()
    if (BaseFragment.checkIsNoEmpty(layout, value, isRequireString)) errors += 1


    // Верхний предел (до): обязательно
    layout = binding.layoutRangeMax
    value = binding.inpRangeMax.text.toString()
    if (BaseFragment.checkIsNoEmpty(layout, value, isRequireString)) errors += 1

    // Единицы измерения: обязательно
    layout = binding.layoutUnits
    value = binding.inpUnits.text.toString()
    if (BaseFragment.checkIsNoEmpty(layout, value, isRequireString)) errors += 1

    return errors != 0
  }

  fun getMeasuringValue() = MeasurementValue(
    name = binding.inpName.text.toString(),
    rangeMin = binding.inpRangeMin.text.toString(),
    rangeMax = binding.inpRangeMax.text.toString(),
    units = binding.inpUnits.text.toString(),
    accuracyClass = binding.inpAccuracyClass.text.toString(),
    graduationPoint = binding.inpGraduationPoint.text.toString(),
  )

  fun setAccessToEdit(hasAccess: Boolean) {
    listOf(
      binding.inpName,
      binding.inpRangeMin,
      binding.inpRangeMax,
      binding.inpUnits,
      binding.inpAccuracyClass,
      binding.inpGraduationPoint
    ).forEach { it.isEnabled = hasAccess }
  }
}