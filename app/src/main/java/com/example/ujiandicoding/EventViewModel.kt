package com.example.ujiandicoding

import androidx.lifecycle.ViewModel
import com.example.ujiandicoding.data.EventRepository

class EventViewModel(private val eventRepository: EventRepository) : ViewModel() {
    fun getUpcomingEvents() = eventRepository.getUpcomingEvents()
    fun getFinishedEvents() = eventRepository.getFinishedEvents()
}