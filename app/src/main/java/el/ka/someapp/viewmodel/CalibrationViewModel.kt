package el.ka.someapp.viewmodel

import android.app.Application
import el.ka.someapp.data.model.measuring.Calibration
import el.ka.someapp.data.model.measuring.MeasuringPart

class CalibrationViewModel(application: Application) : MeasuringPartViewModel(application) {
  override var measuringPart = MeasuringPart.CALIBRATION

  override fun getMeasuringPartRealization() = Calibration(
    interval = _interval.value!!,
    dateLast = _lastDate.value,
    dateNext = _nextDate.value,
  )
}