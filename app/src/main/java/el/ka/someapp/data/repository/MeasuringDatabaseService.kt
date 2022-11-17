package el.ka.someapp.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.measuring.Measuring
import kotlinx.coroutines.Deferred

object MeasuringDatabaseService {
  fun createMeasuring(
    measuring: Measuring,
    onSuccess: (String) -> Unit,
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
          onSuccess = { onSuccess(measuringId) }
        )
      }
  }

  fun getMeasuringByIDs(measuringIds: List<String>): List<Deferred<DocumentSnapshot>> =
    FirebaseServices.getDocumentsByIDs(
      measuringIds,
      collectionRef = FirebaseServices.databaseMeasuring
    )
}