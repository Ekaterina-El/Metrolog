package el.ka.someapp.viewmodel

import android.app.Application
import el.ka.someapp.data.model.measuring.MeasuringPart
import el.ka.someapp.data.model.measuring.TO

class TOViewModel(application: Application) : MeasuringPartViewModel(application) {
  override var measuringPart = MeasuringPart.TO

  override fun getMeasuringPartRealization() = TO(
    interval = _interval.value!!,
    dateLast = _lastDate.value,
    dateNext = _nextDate.value,
  )
}