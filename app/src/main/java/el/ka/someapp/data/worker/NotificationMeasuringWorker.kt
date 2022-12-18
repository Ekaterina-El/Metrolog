package el.ka.someapp.data.worker

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import el.ka.someapp.data.model.Node
import el.ka.someapp.data.model.User
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.measuring.Measuring
import el.ka.someapp.data.model.measuring.MeasuringReport
import el.ka.someapp.data.repository.AuthenticationService
import el.ka.someapp.data.repository.MeasuringDatabaseService
import el.ka.someapp.data.repository.NodesDatabaseService
import el.ka.someapp.data.repository.UsersDatabaseService
import el.ka.someapp.general.daysTo
import el.ka.someapp.general.getCurrentTime
import kotlinx.coroutines.awaitAll
import java.util.*

class NotificationMeasuringWorker(context: Context, workerParams: WorkerParameters) :
  CoroutineWorker(context, workerParams) {
  override suspend fun doWork(): Result {
    return try {
      val notifier = Notifier(applicationContext)
      val uid = AuthenticationService.getUserUid()
      val user =
        if (uid != null) UsersDatabaseService.getUserByUid(uid).toObject(User::class.java) else null

      // Выходим если уведомления отключенны или пользователь не найден
      if (!notifier.isAvailable || user == null) return Result.success()

      val nodesId = user.availabilityNodes
      val reports = mutableListOf<MeasuringReport>()
      nodesId.forEach {
        val nodeReports = checkNode(uid!!, it, true, getCurrentTime())
        reports.addAll(nodeReports)
      }

      Result.success()
    } catch (e: Exception) {
      Result.failure()
    }
  }

  private suspend fun checkNode(
    uid: String,
    nodeId: String,
    isCheckCredentials: Boolean = false,
    currentTime: Date
  ): List<MeasuringReport> {
    val node = NodesDatabaseService.getNodeByID(nodeId).toObject(Node::class.java)

    val hasAccess = when (node == null) {
      true -> false
      false -> if (isCheckCredentials) checkCredentials(uid, node) else true
    }

    val reports = mutableListOf<MeasuringReport>()
    if (!hasAccess || node == null) {
      // удаляем у пользователя в availabilityNodes ID предприятия
      UsersDatabaseService.removeAvailabilityNodes(uid, nodeId)
    } else {
      // проверяем сроки СИ
      val nodeReport = checkMeasuringItems(node, "", currentTime)
      if (nodeReport != null) reports.add(nodeReport)

      // проверяем дочерние подразделения без проверки доступа
      node.children.forEach {
        val childrenReports = checkNode(uid, it, isCheckCredentials = false, currentTime)
        reports.addAll(childrenReports)
      }
    }
    return reports
  }

  private suspend fun checkMeasuringItems(
    node: Node,
    companyName: String,
    currentTime: Date
  ): MeasuringReport? {
    var countOfOverdue = 0
    var countOfHalfOverdue = 0

    MeasuringDatabaseService.getMeasuringByIDs(measuringIds = node.measuring)
      .awaitAll()
      .forEach {
        val measuring = it.toObject(Measuring::class.java)!!
        val parts = listOf(
          measuring.passport, measuring.overhaul, measuring.maintenanceRepair,
          measuring.TO, measuring.calibration, measuring.certification, measuring.verification
        )
        for (part in parts) {
          val nextDate = part.dateNext
          val days = if (nextDate != null) currentTime.daysTo(nextDate) else 0
          when {
            days <= 0 -> {
              countOfOverdue++
              return@forEach
            }
            days <= 30 -> {
              countOfHalfOverdue++
              return@forEach
            }
            else -> {}
          }
        }
      }

    return if (countOfOverdue > 0 || countOfHalfOverdue > 0)
      MeasuringReport(node.name, companyName, countOfOverdue, countOfHalfOverdue)
    else null
  }

  // Проверяем, что у пользователя есть должность в подразделении, которая может получать уведомления
  private fun checkCredentials(uid: String, node: Node): Boolean {
    val jobField = node.jobs.firstOrNull { it.userId == uid && it.jobRole != UserRole.READER }
    return jobField != null
  }
}

class Notifier(private val context: Context) {
  val isAvailable: Boolean get() = NotificationManagerCompat.from(context).areNotificationsEnabled()
}