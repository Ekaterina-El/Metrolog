package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.data.model.measuring.MeasuringPart
import el.ka.someapp.data.model.measuring.MeasuringPartRealization
import el.ka.someapp.data.model.measuring.Verification
import kotlin.math.round

class VerificationMeasuringViewModel(application: Application) :
  MeasuringPartViewModel(application) {
  val verificationCodeCSM = MutableLiveData<String>()
  val cost = MutableLiveData<String>()
  val place = MutableLiveData<String>()

  private fun getRoundedCost() = round(cost.value!!.toDouble() * 100) / 100

  override var measuringPart: MeasuringPart
    get() = MeasuringPart.VERIFICATION
    set(_) {}

  override fun getMeasuringPartRealization(): MeasuringPartRealization = Verification(
    interval = _interval.value!!,
    dateLast = _lastDate.value!!,
    dateNext = _nextDate.value!!,
    place = place.value!!,
    cost = getRoundedCost(),
    verificationCodeCSM = verificationCodeCSM.value!!
  )

  override fun loadMeasuring(
    measuring: Measuring,
    viewerRole: UserRole,
    measuringPartRealization: MeasuringPartRealization
  ) {
    super.loadMeasuring(measuring, viewerRole, measuringPartRealization)

    val verification = measuringPartRealization as Verification
    place.value = verification.place
    verificationCodeCSM.value = verification.verificationCodeCSM
    cost.value = verification.cost.toString()
  }
}