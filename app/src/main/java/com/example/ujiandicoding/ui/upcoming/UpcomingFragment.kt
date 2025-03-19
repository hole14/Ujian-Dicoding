package com.example.ujiandicoding.ui.upcoming

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ujiandicoding.EventViewModel
import com.example.ujiandicoding.EventViewModelFactory
import com.example.ujiandicoding.data.Result
import com.example.ujiandicoding.databinding.FragmentUpcomingBinding

class UpcomingFragment : Fragment() {
    private lateinit var binding: FragmentUpcomingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpcomingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = EventViewModelFactory.getInstance(requireActivity())
        val viewModel:EventViewModel by viewModels { factory }

        val adapter = UpcomingAdapter {event ->
            if (event.isFavorite) {
                viewModel.deleteFavoriteEvent(event)
            } else {
                viewModel.saveFavoriteEvent(event)
            }
        }

        binding.rvUpcoming.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcoming.adapter = adapter
        adapter.submitList(emptyList())

        viewModel.getUpcomingEvents().observe(viewLifecycleOwner){result ->
            when (result){
                is Result.Error -> {
                    Log.d("UpcomingFragment", "onViewCreated: ${result.error}")
                }
                Result.Loading -> {

                }
                is Result.Success -> {
                    adapter.submitList(result.data)
                }
            }
        }
    }

}