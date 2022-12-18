package el.ka.someapp.data.worker

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import el.ka.someapp.data.model.Node
import el.ka.someapp.data.model.User
import el.ka.someapp.data.model.UserRole
import el.ka.someapp.data.model.measuring.MeasuringReport
import el.ka.someapp.data.repository.AuthenticationService
import el.ka.someapp.data.repository.NodesDatabaseService
import el.ka.someapp.data.repository.UsersDatabaseService

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
        val nodeReports = checkNode(uid!!, it, true)
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
    isCheckCredentials: Boolean = false
  ): List<MeasuringReport> {
    val node = NodesDatabaseService.getNodeByID(nodeId).toObject(Node::class.java)

    val hasAccess = when (node == null) {
      true -> false
      false -> if (isCheckCredentials) checkCredentials(uid, node) else true
    }

    val reports = mutableListOf<MeasuringReport>()
    if (!hasAccess) {
      // удаляем у пользователя в availabilityNodes ID предприятия
    } else {
      // проверяем сроки СИ
      // проверяем дочерние подразделения без проверки доступа
    }
    return reports
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