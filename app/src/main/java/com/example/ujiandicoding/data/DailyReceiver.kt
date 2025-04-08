package com.example.ujiandicoding.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.util.Log
import android.widget.Toast
import com.example.ujiandicoding.data.retrofit.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DailyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val apiService = ApiConfig.getApiService()
                val respone = apiService.getLatestEvent()

                if (respone.listEvents != null) {
                    val latest = respone.listEvents[0]
                    NotificationHelper.showNotification(context, "Jangan lewatkan", "Event yang sedang berlangsung ${latest?.name}")
                } else {
                    NotificationHelper.showNotification(context, "Daily Reminder", "Tidak ada event yang sedang berlangsung")
                }
            } catch (e: Exception) {
                Log.d("DailyReceiver", "onReceive: ${e.message.toString()}")
                NotificationHelper.showNotification(context, "Daily Reminder", "Tidak ada koneksi internet")
            }
        }
    }
    fun setDailyReminder(context: Context, hour: Int, minute: Int){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_IMMUTABLE)

        val calender = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        if (calender.timeInMillis < System.currentTimeMillis()) {
            calender.add(Calendar.DAY_OF_MONTH, 1)
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calender.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(context, "Daily reminder set at $hour:$minute", Toast.LENGTH_SHORT).show()
    }
    fun cancelReminder(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "Daily reminder canceled", Toast.LENGTH_SHORT).show()
    }
}