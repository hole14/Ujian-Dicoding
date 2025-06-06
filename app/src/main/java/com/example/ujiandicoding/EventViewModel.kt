package com.example.ujiandicoding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ujiandicoding.data.EventRepository
import com.example.ujiandicoding.data.entity.EventEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventViewModel(private val eventRepository: EventRepository) : ViewModel() {
    fun getUpcomingEvents() = eventRepository.getUpcomingEvents()
    fun getFinishedEvents() = eventRepository.getFinishedEvents()

    private val _query = MutableLiveData<List<EventEntity>>()
    val query: LiveData<List<EventEntity>> get() = _query

    fun searchEvents(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = eventRepository.searchEvents(query)
            _query.postValue(result)
        }
    }
}