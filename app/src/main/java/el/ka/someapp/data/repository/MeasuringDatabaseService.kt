package el.ka.someapp.data.repository

import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.measuring.Measuring

object MeasuringDatabaseService {
  fun createMeasuring(
    measuring: Measuring,
    onSuccess: () -> Unit,
    onFailure: (ErrorApp) -> Unit
  ) {
    FirebaseServices.databaseMeasuring
      .add(measuring)
      .addOnFailureListener { onFailure(Errors.somethingWrong) }
      .addOnSuccessListener {
        val measuringId = it.id
        NodesDatabaseService.addMeasuringId(
          nodeId = measuring.passport!!.locationIDNode,
          measuringId = measuringId,
          onFailure = { onFailure(Errors.somethingWrong) },
          onSuccess = { onSuccess() }
        )
      }
  }
}