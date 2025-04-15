package com.example.ujiandicoding.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ujiandicoding.R
import com.example.ujiandicoding.data.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale

class ReminderWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    companion object{
        private val TAG = ReminderWorker::class.java.simpleName
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val apiService = ApiConfig.getApiService()
            val response = apiService.getLatestEvent()

            if (!response.listEvents.isNullOrEmpty()) {
                val event = response.listEvents[0]

                event?.beginTime?.let { beginTime ->
                    val eventTime = parseEventTime(beginTime)
                    val now = Calendar.getInstance()

                    val diffMillis = eventTime.timeInMillis - now.timeInMillis
                    val oneHourInMillis = 60 * 60 * 1000

                    if (diffMillis in 0..oneHourInMillis) {
                        NotificationHelper.showNotification(
                            applicationContext,
                            "Event ${event.name}",
                            "Dimulai pada ${formatTime(eventTime)}"
                        )
                        Log.d(TAG, "Notifikasi: ${event.name}")
                    } else {
                        Log.d(TAG, "Belum waktunya notifikasi")
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.d(TAG, "doWork Error: ${e.message}")
            Result.retry()
        }
    }

    private fun formatTime(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun parseEventTime(eventTime: String): Calendar {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date = format.parse(eventTime) ?: Date()
        return Calendar.getInstance().apply { time = date }
    }

    object NotificationHelper {
        private const val CHANNEL_ID = "event_reminder_channel"
        private const val NOTIFICATION_ID = 101

        fun showNotification(context: Context, title: String?, message: String?) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Event Reminder",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

}