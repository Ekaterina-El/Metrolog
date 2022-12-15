package el.ka.someapp.data.model.measuring

import el.ka.someapp.R

enum class MeasuringState(val strRes: Int) {
  CALIBRATION(R.string.calibration),
  VERIFICATION(R.string.verification),
  CERTIFICATION(R.string.certification)
}

