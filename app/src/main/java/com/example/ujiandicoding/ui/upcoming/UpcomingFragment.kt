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

        val adapter = UpcomingAdapter ()

        binding.rvUpcoming.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcoming.adapter = adapter

        viewModel.getUpcomingEvents().observe(viewLifecycleOwner){result ->
            when (result){
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("UpcomingFragment", "onViewCreated: ${result.error}")
                }
                Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(result.data)
                }
            }
        }
    }

}