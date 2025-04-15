package com.example.ujiandicoding.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ujiandicoding.data.EventRepository
import com.example.ujiandicoding.data.retrofit.ApiConfig
import com.example.ujiandicoding.data.retrofit.ApiService
import com.example.ujiandicoding.data.room.EventDatabase
import com.example.ujiandicoding.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var eventRepository: EventRepository
    private lateinit var adapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = EventDatabase.getInstance(requireContext())
        val eventDao = database.eventDao()
        eventRepository = EventRepository.getInstance(eventDao, ApiConfig.getApiService())

        adapter = FavoriteAdapter()
        binding.rvFavorite.adapter = adapter
        binding.rvFavorite.layoutManager = LinearLayoutManager(requireContext())

        eventRepository.getFavoriteEvents().observe(viewLifecycleOwner) { favoriteEvents ->
            binding.progressBar.visibility = View.GONE
            if (favoriteEvents.isNullOrEmpty()) {
                binding.rvFavorite.visibility = View.GONE
            } else {
                binding.rvFavorite.visibility = View.VISIBLE
                adapter.submitList(favoriteEvents)
            }
        }
    }
}