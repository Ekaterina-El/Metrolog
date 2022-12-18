package el.ka.someapp.data.worker

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationMeasuringWorker(context: Context, workerParams: WorkerParameters) :
  Worker(context, workerParams) {
  override fun doWork(): Result {
    return try {
      val notifier = Notifier(applicationContext)
      if (!notifier.isAvailable) return Result.success()

      Result.success()
    } catch (e: Exception) {
      Result.failure()
    }
  }
}

class Notifier(private val context: Context) {
  val isAvailable: Boolean get() = NotificationManagerCompat.from(context).areNotificationsEnabled()
}