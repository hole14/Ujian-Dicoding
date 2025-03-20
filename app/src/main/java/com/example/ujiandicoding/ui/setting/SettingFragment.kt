package com.example.ujiandicoding.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import com.example.ujiandicoding.databinding.FragmentSettingBinding
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private val viewModel: SettingViewModel by viewModels{
        ViewModelFactory(SettingPreference.getInstance(requireContext().dataStore))
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getThemeSetting().observe(viewLifecycleOwner){ isDarkModeActive ->
            binding.switchTheme.isChecked = isDarkModeActive
            binding.switchTheme.text = if (isDarkModeActive) "Light Mode" else "Dark Mode"
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveThemeSetting(isChecked)
        }

        viewModel.getReminderSetting().observe(viewLifecycleOwner){ isReminderActive ->
            binding.switchDaily.isChecked = isReminderActive
        }
        binding.switchDaily.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveReminderSetting(isChecked)
            if (isChecked) {
                dailyReminder()
            } else {
                WorkManager.getInstance(requireContext()).cancelUniqueWork("DailyReminder")
            }
        }
    }

    private fun dailyReminder(){
        val worker = PeriodicWorkRequestBuilder<Worker>(1, TimeUnit.DAYS)
            .setInitialDelay(10, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork("DailyReminder",
            ExistingPeriodicWorkPolicy.UPDATE, worker)
    }

}