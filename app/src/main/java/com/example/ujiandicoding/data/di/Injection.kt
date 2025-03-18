package com.example.ujiandicoding.data.di

import android.content.Context
import com.example.ujiandicoding.data.EventRepository
import com.example.ujiandicoding.data.retrofit.ApiConfig
import com.example.ujiandicoding.data.room.EventDatabase

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        return EventRepository.getInstance(dao, apiService)
    }
}