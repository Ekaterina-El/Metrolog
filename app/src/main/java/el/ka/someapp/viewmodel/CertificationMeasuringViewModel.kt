package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.measuring.Certification
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.data.model.measuring.MeasuringPart
import el.ka.someapp.data.model.measuring.MeasuringPartRealization


class CertificationMeasuringViewModel(application: Application) :
  MeasuringPartViewModel(application) {
  val place = MutableLiveData<String>()

  override var measuringPart: MeasuringPart
    get() = MeasuringPart.CERTIFICATION
    set(value) {}

  override fun getMeasuringPartRealization(): MeasuringPartRealization = Certification(
    interval = _interval.value!!,
    dateLast = _lastDate.value!!,
    dateNext = _nextDate.value!!,
    place = place.value!!,
  )

  override fun loadMeasuring(
    measuring: Measuring,
    viewerRole: UserRole,
    measuringPartRealization: MeasuringPartRealization
  ) {
    super.loadMeasuring(measuring, viewerRole, measuringPartRealization)
    place.value = (measuringPartRealization as Certification).place
  }
}