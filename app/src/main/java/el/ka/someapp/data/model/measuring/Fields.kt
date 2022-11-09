package el.ka.someapp.data.model.measuring

object Fields {
  val measuringTypeVariables = listOf(
    MeasuringType.MEASURE,
    MeasuringType.MEASURING_DEVICE,
    MeasuringType.MEASURING_TRANSDUCERS,
    MeasuringType.MEASURING_SYSTEM,
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
    MeasuringState.CALIBRATION,
    MeasuringState.VERIFICATION,
    MeasuringState.CERTIFICATION
  )

  val measurementConditionVariables = listOf(
    MeasuringCondition.IN_WORK,
    MeasuringCondition.REPAIR,
    MeasuringCondition.MOTHBALLED
  )
}