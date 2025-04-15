package com.example.ujiandicoding.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ujiandicoding.data.entity.EventEntity

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: List<EventEntity>)

    @Query("SELECT * FROM event WHERE name LIKE '%' || :query || '%'")
    suspend fun getSearchEvent(query: String): List<EventEntity>

    @Query("SELECT * FROM event WHERE active = 1")
    fun getUpcomingEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE active = 0")
    fun getFinishedEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE isFavorite = 1")
    fun getFavoriteEvents(): LiveData<List<EventEntity>>

    @Query("UPDATE event SET isFavorite = :isFavorite WHERE id = :eventId")
    suspend fun updateFavoriteStatus(eventId: Int, isFavorite: Boolean)

    @Query("SELECT * FROM event WHERE id = :eventId LIMIT 1")
    fun getEventById(eventId: Int): LiveData<EventEntity>

}