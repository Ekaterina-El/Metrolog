package el.ka.someapp.data.model.measuring

import el.ka.someapp.data.model.UserRole

object Fields {
  val measuringTypeVariables = listOf(
    MeasuringKind.MEASURE,
    MeasuringKind.MEASURING_DEVICE,
    MeasuringKind.MEASURING_TRANSDUCERS,
    MeasuringKind.MEASURING_SYSTEM,
  )

  val measuringCategoryVariables = listOf(
    MeasuringCategory.WORKING,
    MeasuringCategory.REFERENCE,
    MeasuringCategory.STANDARDS,
  )

  val measurementTypeVariables = listOf(
    MeasurementType.GEOMETRIC_QUANTITIES,
    MeasurementType.MECHANICAL_QUANTITIES,
    MeasurementType.FLOW__FLOW_RARE__LEVEL__VOLUME_OF_SUBSTANCES,
    MeasurementType.PRESSURE,
    MeasurementType.PHYSICAL_CHEMICAL,
    MeasurementType.THERMOPHYSICAL_TEMPERATURE,
    MeasurementType.TIME_FREQUENCY,
    MeasurementType.ELECTRICAL_MAGNETIC_QUANTITIES,
    MeasurementType.RADIOELECTRONIC,
    MeasurementType.ACOUSTIC_QUANTITIES,
    MeasurementType.OPTICAL_OPTICALPHYSICAL,
    MeasurementType.IONIZING_RADIATION__NUCLEAR_CONSTANTS
  )

  val measurementStatusVariables = listOf(
    MeasuringState.NOTHING,
    MeasuringState.CALIBRATION,
    MeasuringState.VERIFICATION,
    MeasuringState.CERTIFICATION
  )

  val measurementConditionVariables = listOf(
    MeasuringCondition.IN_WORK,
    MeasuringCondition.REPAIR,
    MeasuringCondition.MOTHBALLED
  )

  val rolesTypeVariables = listOf(
    UserRole.READER,
    UserRole.EDITOR_2,
    UserRole.EDITOR_1,
  )

}