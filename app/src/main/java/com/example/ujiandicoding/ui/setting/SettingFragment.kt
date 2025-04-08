package com.example.ujiandicoding.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ujiandicoding.data.ReminderWorker
import com.example.ujiandicoding.databinding.FragmentSettingBinding
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private val viewModel: SettingViewModel by viewModels{
        ViewModelFactory(SettingPreference.getInstance(requireContext().dataStore))
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(requireContext(), "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
        }
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
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
                dailyReminder()
            } else {
                WorkManager.getInstance(requireContext()).cancelUniqueWork("DailyReminder")
            }
        }
    }

    private fun dailyReminder(){
        val worker = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(10, TimeUnit.MINUTES)
            .build()

        val workManager = WorkManager.getInstance(requireContext())
        workManager.enqueue(worker)

        workManager.getWorkInfoByIdLiveData(worker.id).observe(viewLifecycleOwner){ workInfo ->
            if (workInfo?.state?.isFinished == true){
                dailyReminder()
            }
        }
    }

}