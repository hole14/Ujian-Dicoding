package com.example.ujiandicoding.ui.upcoming

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ujiandicoding.EventViewModel
import com.example.ujiandicoding.EventViewModelFactory
import com.example.ujiandicoding.data.EventRepository
import com.example.ujiandicoding.data.Result
import com.example.ujiandicoding.data.retrofit.ApiConfig
import com.example.ujiandicoding.data.room.EventDatabase
import com.example.ujiandicoding.databinding.FragmentUpcomingBinding
import com.example.ujiandicoding.ui.favorite.FavoriteViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class UpcomingFragment : Fragment() {
    private lateinit var binding: FragmentUpcomingBinding
    private lateinit var favoriteViewModel: FavoriteViewModel
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
        favoriteViewModel = ViewModelProvider(requireActivity())[FavoriteViewModel::class.java]

        val adapter = UpcomingAdapter()

        binding.rvUpcoming.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcoming.adapter = adapter

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