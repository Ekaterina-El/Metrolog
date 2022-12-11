package el.ka.someapp.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.measuring.MaintenanceRepair
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.data.model.measuring.MeasuringPart
import el.ka.someapp.data.model.measuring.MeasuringPartRealization

class MaintenanceRepairViewModel(application: Application) : MeasuringPartViewModel(application) {
  override var measuringPart = MeasuringPart.MAINTENANCE_REPAIR

  val laboratory = MutableLiveData<String>()
  val place = MutableLiveData<String>()


  override fun getMeasuringPartRealization(): MeasuringPartRealization = MaintenanceRepair(
    interval = _interval.value!!,
    dateLast = _lastDate.value,
    dateNext = _nextDate.value,
    place = place.value!!,
    laboratory = laboratory.value!!
  )

  override fun loadMeasuring(
    measuring: Measuring,
    viewerRole: UserRole,
    measuringPartRealization: MeasuringPartRealization
  ) {
    super.loadMeasuring(measuring, viewerRole, measuringPartRealization)

    val maintenanceRepair = measuringPartRealization as MaintenanceRepair
    laboratory.value = maintenanceRepair.laboratory
    place.value = maintenanceRepair.place
  }
}
