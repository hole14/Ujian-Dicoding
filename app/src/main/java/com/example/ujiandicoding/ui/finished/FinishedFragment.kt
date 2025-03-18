package com.example.ujiandicoding.ui.finished

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ujiandicoding.EventViewModel
import com.example.ujiandicoding.EventViewModelFactory
import com.example.ujiandicoding.R
import com.example.ujiandicoding.data.Result
import com.example.ujiandicoding.databinding.FragmentFinishedBinding

class FinishedFragment : Fragment() {
    private lateinit var binding: FragmentFinishedBinding
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

        val adapter = FinishedAdapter()
        binding.rvFinished.layoutManager = LinearLayoutManager(requireContext())


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
        binding.rvFinished.adapter = adapter

    }
}