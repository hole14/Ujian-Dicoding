package com.example.ujiandicoding.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ujiandicoding.data.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: List<EventEntity>)

    @Query("SELECT * FROM event")
    fun getAllEvent(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE name LIKE '%' || :query || '%'")
    suspend fun getSearchEvent(query: String): List<EventEntity>

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("DELETE FROM event WHERE isFavorite = 0")
    fun deleteAll()

    @Query("SELECT * FROM event where isFavorite = 1")
    fun getEventFavorites(): LiveData<List<EventEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM event WHERE id = :id AND isFavorite = 1)")
    fun isEventFavorited(id: Int): Boolean

}