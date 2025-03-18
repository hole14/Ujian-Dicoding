package com.example.ujiandicoding.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.ujiandicoding.data.entity.EventEntity
import com.example.ujiandicoding.data.retrofit.ApiService
import com.example.ujiandicoding.data.room.EventDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventRepository private constructor(
    private val eventDao: EventDao,
    private val apiService: ApiService
){
    fun getUpcomingEvents(): LiveData<Result<List<EventEntity>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val respone = apiService.getUpcomingEvents()
            val listEvent = respone.listEvents
            val eventList = listEvent?.map { event ->
                val isFavorite = eventDao.isEventFavorited(event?.id!!)
                EventEntity(
                    event.id,
                    event.name!!,
                    event.summary!!,
                    event.ownerName!!,
                    event.cityName!!,
                    event.beginTime!!,
                    event.endTime!!,
                    event.imageLogo!!,
                    event.mediaCover!!,
                    event.link!!,
                    event.description!!,
                    event.category!!,
                    event.quota!!,
                    event.registrants!!,
                    isFavorite
                )
            }
            eventDao.deleteAll()
            eventList?.let { eventDao.insertEvent(it) }
        } catch (e: Exception) {
            Log.d("EventRepository", "getUpcomingEvents: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<EventEntity>>> = eventDao.getAllEvent().map { Result.Success(it) }
        emitSource(localData)

    }
    fun getFinishedEvents(): LiveData<Result<List<EventEntity>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val respone = apiService.getFinishedEvents()
            val listEvent = respone.listEvents
            val eventList = listEvent?.map { event ->
                val isFavorite = eventDao.isEventFavorited(event?.id!!)
                EventEntity(
                    event.id,
                    event.name!!,
                    event.summary!!,
                    event.ownerName!!,
                    event.cityName!!,
                    event.beginTime!!,
                    event.endTime!!,
                    event.imageLogo!!,
                    event.mediaCover!!,
                    event.link!!,
                    event.description!!,
                    event.category!!,
                    event.quota!!,
                    event.registrants!!,
                    isFavorite
                )
            }
            eventDao.deleteAll()
            eventList?.let { eventDao.insertEvent(it) }
        } catch (e: Exception) {
            Log.d("EventRepository", "getFinishedEvents: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<EventEntity>>> = eventDao.getAllEvent().map { Result.Success(it) }
        emitSource(localData)
    }
    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(eventDao: EventDao, apiService: ApiService): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(eventDao, apiService).also { instance = it }
            }
    }
}