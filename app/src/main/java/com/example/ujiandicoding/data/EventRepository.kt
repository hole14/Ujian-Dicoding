package com.example.ujiandicoding.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.ujiandicoding.data.entity.EventEntity
import com.example.ujiandicoding.data.retrofit.ApiService
import com.example.ujiandicoding.data.room.EventDao
import kotlinx.coroutines.Dispatchers

class EventRepository private constructor(
    private val eventDao: EventDao,
    private val apiService: ApiService
){
    fun getUpcomingEvents(): LiveData<Result<List<EventEntity>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val respone = apiService.getUpcomingEvents(1)
            val listEvent = respone.listEvents
            val eventList = listEvent?.map { event ->
                EventEntity(
                    event?.id!!,
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
                    false,
                    1
                )
            }
            eventList?.let { eventDao.insertEvent(it) }
        } catch (e: Exception) {
            Log.d("EventRepository", "getUpcomingEvents: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<EventEntity>>> = eventDao.getUpcomingEvents().map { Result.Success(it) }
        emitSource(localData)
    }
    fun getFinishedEvents(): LiveData<Result<List<EventEntity>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val respone = apiService.getFinishedEvents(0)
            val listEvent = respone.listEvents
            val eventList = listEvent?.map { event ->
                EventEntity(
                    event?.id!!,
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
                    false,
                    0
                )
            }
            eventList?.let { eventDao.insertEvent(it) }

        } catch (e: Exception) {
            Log.d("EventRepository", "getFinishedEvents: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<EventEntity>>> = eventDao.getFinishedEvents().map { Result.Success(it) }
        emitSource(localData)
    }
    suspend fun searchEvents(query: String): List<EventEntity>{
        return eventDao.getSearchEvent(query)
    }

    fun getFavoriteEvents() = eventDao.getFavoriteEvents()

    suspend fun toggleFavorite(event: EventEntity) {
        event.isFavorite = !event.isFavorite
        eventDao.updateFavoriteStatus(event.id, event.isFavorite)
    }

    fun getEventById(eventId: Int): LiveData<EventEntity> {
        return eventDao.getEventById(eventId)
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