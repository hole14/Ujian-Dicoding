package com.example.ujiandicoding.ui.favorite

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ujiandicoding.EventViewModel
import com.example.ujiandicoding.EventViewModelFactory
import com.example.ujiandicoding.R
import com.example.ujiandicoding.databinding.FragmentFavoriteBinding
import com.example.ujiandicoding.ui.finished.FinishedAdapter
import com.example.ujiandicoding.ui.upcoming.UpcomingAdapter

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = EventViewModelFactory.getInstance(requireActivity())
        val viewModel: EventViewModel by viewModels { factory }

        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) { events ->
            val adapter = UpcomingAdapter { event ->
                if (event.isFavorite) {
                    viewModel.deleteFavoriteEvent(event)
                } else {
                    viewModel.saveFavoriteEvent(event)
                }
            }
            binding.rvFavorite.layoutManager = LinearLayoutManager(requireContext())
            binding.rvFavorite.adapter = adapter
            adapter.submitList(events)
        }
        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) { events ->
            val adapter = FinishedAdapter { event ->
                if (event.isFavorite) {
                    viewModel.deleteFavoriteEvent(event)
                } else {
                    viewModel.saveFavoriteEvent(event)
                }
            }
            binding.rvFavorite.layoutManager = LinearLayoutManager(requireContext())
            binding.rvFavorite.adapter = adapter
            adapter.submitList(events)
        }
    }
}