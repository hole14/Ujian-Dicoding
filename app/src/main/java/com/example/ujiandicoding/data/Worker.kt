package com.example.ujiandicoding.data

import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ujiandicoding.data.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Worker(context: Context, workerParams: WorkerParameters): CoroutineWorker(context, workerParams) {
    companion object{
        private val TAG = "Worker"::class.java.simpleName

    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO){
            try {
                val apiService = ApiConfig.getApiService()
                val respone = apiService.getLatestEvent()

                if (!respone.listEvents.isNullOrEmpty()){
                    val event = respone.listEvents[0]
                    val eventTime = parseEventTime(event?.beginTime!!)

                    eventTime.add(Calendar.MINUTE, -10)

                    val now = Calendar.getInstance()

                    if (eventTime.timeInMillis < now.timeInMillis){
                        NotificationHelper.showNotification(
                            applicationContext,
                            "Event ${event.name}",
                            "Dimulai pada ${formatTime(eventTime)}"
                        )
                        Log.d(TAG, "doWork: ${event.name}")
                    } else {
                        Log.d(TAG, "doWork: Tidak ada event yang datang")
                    }
                }
                Result.success()
            } catch (e: Exception){
                Log.d(TAG, "doWork: ${e.message.toString()}")
                Result.retry()
            }
        }
    }

    private fun parseEventTime(eventTime: String): Calendar {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = format.parse(eventTime) ?: Date()
        val calendar = Calendar.getInstance().apply { time = date }
        return calendar
    }

    private fun formatTime(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}