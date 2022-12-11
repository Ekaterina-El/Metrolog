package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.data.model.measuring.MeasuringPart
import el.ka.someapp.data.model.measuring.MeasuringPartRealization
import el.ka.someapp.data.model.measuring.Overhaul

class OverhaulViewModel(application: Application) : MeasuringPartViewModel(application) {
  val place = MutableLiveData<String>()
  val laboratory = MutableLiveData<String>()

  override var measuringPart = MeasuringPart.OVERHAUL

  override fun getMeasuringPartRealization(): MeasuringPartRealization = Overhaul(
    interval = _interval.value!!,
    dateLast = _lastDate.value!!,
    dateNext = _nextDate.value!!,
    place = place.value!!,
    laboratory = laboratory.value!!,
  )

  override fun loadMeasuring(
    measuring: Measuring,
    viewerRole: UserRole,
    measuringPartRealization: MeasuringPartRealization
  ) {
    super.loadMeasuring(measuring, viewerRole, measuringPartRealization)

    val overhaul = measuringPartRealization as Overhaul
    place.value = overhaul.place
    laboratory.value = overhaul.laboratory
  }
}