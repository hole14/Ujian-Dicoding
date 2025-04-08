package com.example.ujiandicoding.data

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
            if (!response.listEvents.isNullOrEmpty()){
                val event = response.listEvents[0]

                event?.beginTime?.let { beginTime ->
                    val eventTime = parseEventTime(beginTime)

                    val tomorrow = Calendar.getInstance()
                    tomorrow.add(Calendar.DAY_OF_YEAR, 1)

                    val besok =
                        eventTime.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR) &&
                        eventTime.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR)

                    if(besok){
                        NotificationHelper.showNotification(
                            applicationContext, "Event Besok : ${event.name}", "Dimulai pada ${formatTime(eventTime)}"
                        )
                        Log.d(TAG,"Notifikasi Event Besok: ${event.name}")
                    }else{
                        Log.d(TAG, "Tidak ada event yang dimulai besok")
                    }
                }
            }
            Result.success()
        }catch (e: Exception){
            Log.d(TAG, "doWork: ${e.message.toString()}")
            Result.retry()
        }
    }

    private fun formatTime(calender: Calendar): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(calender.time)
    }

    private fun parseEventTime(eventTime: String): Calendar {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date = format.parse(eventTime) ?: Date()
        return Calendar.getInstance().apply { time = date }
    }

}