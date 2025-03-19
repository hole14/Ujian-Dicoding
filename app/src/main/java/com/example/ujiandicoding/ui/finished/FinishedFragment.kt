package com.example.ujiandicoding.ui.finished

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ujiandicoding.EventViewModel
import com.example.ujiandicoding.EventViewModelFactory
import com.example.ujiandicoding.R
import com.example.ujiandicoding.data.Result
import com.example.ujiandicoding.data.entity.EventEntity
import com.example.ujiandicoding.data.respone.ListEventsItem
import com.example.ujiandicoding.databinding.FragmentFinishedBinding

class FinishedFragment : Fragment() {
    private lateinit var binding: FragmentFinishedBinding
    private var search = listOf<ListEventsItem>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = EventViewModelFactory.getInstance(requireActivity())
        val viewModel: EventViewModel by viewModels {
            factory
        }

        val adapter = FinishedAdapter { event ->
            if (event.isFavorite) {
                viewModel.deleteFavoriteEvent(event)
            } else {
                viewModel.saveFavoriteEvent(event)
            }
        }
        binding.rvFinished.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFinished.adapter = adapter

        viewModel.getFinishedEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Error -> {
                    Log.d("FinishedFragment", "onViewCreated: ${result.error}")
                }
                Result.Loading -> {}
                is Result.Success -> {
                    adapter.submitList(result.data)
                }
            }
        }
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.searchEvents(newText)
                }
                return true
            }

        })
        viewModel.query.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}