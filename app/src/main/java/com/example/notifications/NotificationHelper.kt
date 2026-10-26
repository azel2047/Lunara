package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.CyclePhase
import com.example.data.model.CyclePrediction

object NotificationHelper {

    private const val CHANNEL_ID = "lunara_cycle_reminders"
    private const val CHANNEL_NAME = "Pengingat Siklus Lunara"
    private const val CHANNEL_DESC = "Notifikasi cerdas untuk estimasi haid, masa subur, dan fase PMS."

    private const val NOTIF_ID_PERIOD = 1001
    private const val NOTIF_ID_FERTILE = 1002
    private const val NOTIF_ID_PMS = 1003
    private const val NOTIF_ID_TEST = 1004

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendPeriodReminderNotification(context: Context, daysRemaining: Int, isDisguised: Boolean) {
        val title = "Pengingat Lunara"
        val message = if (isDisguised) {
            "Catatan kesehatan pribadi: Tetap jaga asupan nutrisi dan kenyamanan tubuhmu hari ini."
        } else {
            "Catatan ritme siklus: Estimasi fase baru dalam $daysRemaining hari lagi. Periksa persiapanmu di aplikasi."
        }
        showNotification(context, NOTIF_ID_PERIOD, title, message)
    }

    fun sendFertileWindowReminder(context: Context, isDisguised: Boolean) {
        val title = "Pengingat Lunara"
        val message = if (isDisguised) {
            "Kondisi tubuhmu berada pada ritme optimal hari ini. Waktu terbaik untuk beraktivitas!"
        } else {
            "Ritme tubuh: Kondisi energi fisik dan vitalitasmu berada di titik optimal hari ini."
        }
        showNotification(context, NOTIF_ID_FERTILE, title, message)
    }

    fun sendPmsReminder(context: Context, isDisguised: Boolean) {
        val title = "Pengingat Lunara"
        val message = if (isDisguised) {
            "Luangkan waktu 10 menit untuk relaksasi, minum teh hangat, dan istirahat yang cukup."
        } else {
            "Kenyamanan tubuh: Luangkan waktu untuk relaksasi, hidrasi seimbang, dan istirahat cukup."
        }
        showNotification(context, NOTIF_ID_PMS, title, message)
    }

    fun sendTestNotification(context: Context, isDisguised: Boolean) {
        val title = "Pengingat Lunara"
        val message = "Pengingat privat aktif. Data tetap tersimpan aman di perangkat."
        showNotification(context, NOTIF_ID_TEST, title, message)
    }

    fun checkAndTriggerReminders(
        context: Context,
        prediction: CyclePrediction,
        notificationsEnabled: Boolean,
        isDisguised: Boolean
    ) {
        if (!notificationsEnabled) return

        when (prediction.currentPhase) {
            CyclePhase.OVULATION -> sendFertileWindowReminder(context, isDisguised)
            CyclePhase.LUTEAL -> {
                if (prediction.nextPeriodDaysRemaining <= 5) {
                    sendPmsReminder(context, isDisguised)
                }
            }
            CyclePhase.FOLLICULAR -> {
                if (prediction.nextPeriodDaysRemaining <= 3) {
                    sendPeriodReminderNotification(context, prediction.nextPeriodDaysRemaining, isDisguised)
                }
            }
            CyclePhase.MENSTRUATION -> {
                // User is in period
            }
        }
    }

    private fun showNotification(context: Context, notificationId: Int, title: String, content: String) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Lockscreen public version: strictly neutral without any cycle or personal hint
        val publicNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.lunara_icon_1792844631979)
            .setContentTitle("Pengingat Lunara")
            .setContentText("Catatan kesejahteraan harian.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(0xFFD2386A.toInt())
            .build()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.lunara_icon_1792844631979)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(publicNotification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(0xFFD2386A.toInt())

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Permission not granted on Android 13+
        }
    }
}
