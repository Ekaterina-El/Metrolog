package el.ka.someapp.data.model.measuring

import el.ka.someapp.R

enum class MeasuringActionType(strId: Int) {
  CREATED(R.string.created_measuring),
  EDITED_PASSPORT(R.string.edited_passport_measuring),
  EDITED_MAINTENANCE_REPAIR(R.string.edited_maintenance_repair),
  EDITED_TO(R.string.edited_measuring_to),
  EDITED_CALIBRATION(R.string.edited_calibration),
  EDITED_VERIFICATION(R.string.edited_verification),
  EDITED_OVERHAUL(R.string.edited_overhaul),
  EDITED_CERTIFICATION(R.string.edit_certification)
}