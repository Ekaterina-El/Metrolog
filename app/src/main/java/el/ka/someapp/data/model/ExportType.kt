package el.ka.someapp.data.model

import el.ka.someapp.data.model.measuring.MeasuringPart

enum class ExportType {
  GENERAL,
  MAINTENANCE_REPAIR,
  TO,
  OVERHAUL,
  CALIBRATION,
  CERTIFICATION,
  VERIFICATION;

  fun toMeasuringPart(): MeasuringPart = when (this) {
    GENERAL -> MeasuringPart.PASSPORT
    MAINTENANCE_REPAIR -> MeasuringPart.MAINTENANCE_REPAIR
    TO -> MeasuringPart.TO
    OVERHAUL -> MeasuringPart.OVERHAUL
    CALIBRATION -> MeasuringPart.CALIBRATION
    CERTIFICATION -> MeasuringPart.CERTIFICATION
    VERIFICATION -> MeasuringPart.VERIFICATION
  }
}