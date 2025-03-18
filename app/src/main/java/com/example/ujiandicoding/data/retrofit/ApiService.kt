package com.example.ujiandicoding.data.retrofit

import com.example.ujiandicoding.data.respone.DetailEventsResponse
import com.example.ujiandicoding.data.respone.EventsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getUpcomingEvents(@Query("active") active: Int = 1): EventsResponse

    @GET("events")
    suspend fun getFinishedEvents(@Query("active") active: Int = 0): EventsResponse

    @GET("events")
    suspend fun getSearchEvents(@Query("active") active: Int = -1, @Query("q") q: String): EventsResponse

    @GET("events/{id}")
    suspend fun getDetailEvents(@Path("id") id: Int): DetailEventsResponse

}