package com.example.ujiandicoding.ui.finished

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
    ): View {
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
        binding.rvFinished.adapter = adapter

        viewModel.getFinishedEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("FinishedFragment", "onViewCreated: ${result.error}")
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

        val searchText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchText.setHintTextColor(resources.getColor(android.R.color.darker_gray))
        searchText.setTextColor(resources.getColor(android.R.color.black))

        val searchIcon = binding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.search_icon_color), PorterDuff.Mode.SRC_IN)

        val clearIcon = binding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        clearIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.search_icon_color), PorterDuff.Mode.SRC_IN)

        binding.searchView.setIconifiedByDefault(false)
        binding.searchView.queryHint = "Cari Event..."

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

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