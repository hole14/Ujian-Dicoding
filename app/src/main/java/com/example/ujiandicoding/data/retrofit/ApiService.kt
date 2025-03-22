package com.example.ujiandicoding.data.retrofit

import com.example.ujiandicoding.data.respone.EventsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getUpcomingEvents(@Query("active") active: Int): EventsResponse

    @GET("events")
    suspend fun getFinishedEvents(@Query("active") active: Int): EventsResponse

    @GET("events?active=-1&limit=1")
    suspend fun getLatestEvent(): EventsResponse

}