package el.ka.someapp.data.model

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import el.ka.someapp.MainActivity
import el.ka.someapp.R
import el.ka.someapp.data.model.measuring.MeasuringReport

class Notifier(private val context: Context) {
  val isAvailable: Boolean get() = NotificationManagerCompat.from(context).areNotificationsEnabled()
  private var id = 10
  val notificationManager =
    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  private fun initChannel() {
    val channel = NotificationChannel(
      NOTIFICATION_CHANNEL,
      NOTIFICATION_NAME,
      NotificationManager.IMPORTANCE_HIGH
    )
    notificationManager.createNotificationChannel(channel)
  }

  fun notifyMeasuring(report: List<MeasuringReport>) {
    initChannel()

    val notificationManager = NotificationManagerCompat.from(context)

    report.forEach {
//      val id: Int = ((getCurrentTime().time / 1000L) % Integer.MAX_VALUE).toInt()
      id++
      val notification = createNotification(id, it)
      notificationManager.notify(id, notification)
    }
  }

  private fun createNotification(id: Int, report: MeasuringReport): Notification {
    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    intent.putExtra(NOTIFICATION_ID, id)

    val title = context.getString(R.string.notification_report_title)

    val content = if (report.nodeName != null)
      context.getString(
        R.string.notification_report_subtitle,
        report.nodeName,
        report.companyName,
        report.countOfOverdue,
        report.countOfHalfOverdue
      )
    else context.getString(
      R.string.notification_report_subtitle_without_node_name,
      report.companyName,
      report.countOfOverdue,
      report.countOfHalfOverdue
    )

    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
      .setSmallIcon(R.drawable.ic_logo)
      .setContentTitle(title)
      .setContentText(content)
      .setDefaults(Notification.DEFAULT_ALL)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .setStyle(
        NotificationCompat.BigTextStyle()
          .bigText(content)
      )

    notification.priority = Notification.PRIORITY_MAX
    notification.setChannelId(NOTIFICATION_CHANNEL)
    return notification.build()
  }

  companion object {
    const val NOTIFICATION_ID = "metrolog_notification_id"
    const val NOTIFICATION_CHANNEL = "metrolog_channel_01"
    const val NOTIFICATION_NAME = "metrolog_name_01"
  }
}