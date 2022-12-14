package el.ka.someapp.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import el.ka.someapp.data.model.ErrorApp
import el.ka.someapp.data.model.Errors
import el.ka.someapp.data.model.measuring.*
import el.ka.someapp.general.getCurrentTime
import kotlinx.coroutines.Deferred
import java.util.*

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
          nodeId = measuring.passport.locationIDNode,
          measuringId = measuringId,
          onFailure = { onFailure(Errors.somethingWrong) },
          onSuccess = {
            changeMeasuringHistory(
              measuringId,
              getHistoryItem(MeasuringActionType.CREATED),
              onSuccess = { onSuccess(measuringId) },
              onFailure = { onFailure(Errors.somethingWrong) })
          }
        )
      }
  }

  fun getMeasuringByIDs(measuringIds: List<String>): List<Deferred<DocumentSnapshot>> =
    FirebaseServices.getDocumentsByIDs(
      measuringIds,
      collectionRef = FirebaseServices.databaseMeasuring
    )

  fun deleteMeasuring(
    measuringId: String,
    locationNodeId: String? = null,
    onFailure: () -> Unit = {},
    onSuccess: () -> Unit = {}
  ) {
    FirebaseServices.databaseMeasuring.document(measuringId)
      .delete()
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener {
        if (locationNodeId == null) onSuccess()
        else NodesDatabaseService.deleteMeasuringIdFromNode(
          locationNodeId,
          measuringId,
          onFailure,
          onSuccess
        )
      }
  }

  @Suppress("IMPLICIT_CAST_TO_ANY")
  fun updateMeasuringPart(
    measuringID: String,
    part: MeasuringPart,
    value: Any,
    onFailure: () -> Unit,
    onSuccess: (MeasuringHistoryItem) -> Unit
  ) {
    val valueToDB = when (part) {
      MeasuringPart.PASSPORT -> value as MeasuringPassport
      MeasuringPart.MAINTENANCE_REPAIR -> value as MaintenanceRepair
      MeasuringPart.OVERHAUL -> value as Overhaul
      MeasuringPart.TO -> value as TO
      MeasuringPart.VERIFICATION -> value as Verification
      MeasuringPart.CERTIFICATION -> value as Certification
      MeasuringPart.CALIBRATION -> value as Calibration
      MeasuringPart.HISTORY -> return
    }
    val partName = part.dbTitle
    val historyItem = getHistoryItem(part.actionType!!)

    FirebaseServices.databaseMeasuring
      .document(measuringID)
      .update(partName, valueToDB)
      .addOnFailureListener { onFailure() }
      .addOnSuccessListener {
        changeMeasuringHistory(
          measuringID,
          historyItem,
          onSuccess = { onSuccess(historyItem) },
          onFailure
        )
      }
  }

  private fun getHistoryItem(action: MeasuringActionType) = MeasuringHistoryItem(
    date = getCurrentTime(),
    userId = AuthenticationService.getUserUid()!!,
    action
  )

  private fun changeMeasuringHistory(
    measuringID: String,
    historyItem: MeasuringHistoryItem,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
  ) {
    val ref = FirebaseServices.databaseMeasuring.document(measuringID)
    FirebaseServices.changeFieldArray(
      ref,
      field = MeasuringPart.HISTORY.dbTitle,
      value = historyItem,
      onSuccess = onSuccess,
      onFailure = onFailure
    )
  }
}