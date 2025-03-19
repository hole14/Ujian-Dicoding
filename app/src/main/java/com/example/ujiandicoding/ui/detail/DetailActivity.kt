package com.example.ujiandicoding.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ujiandicoding.EventViewModel
import com.example.ujiandicoding.R
import com.example.ujiandicoding.data.entity.EventEntity
import com.example.ujiandicoding.databinding.ActivityDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_EVENT = "extra_event"
    }
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: EventViewModel by viewModels ()
    private var event: EventEntity? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        event = intent.getParcelableExtra(EXTRA_EVENT)

        binding.tvJudul.text = event?.name
        binding.tvKategori.text = event?.category
        binding.tvTanggal.text = "📆 ${event?.beginTime?.let { formatDate(it) }} - ${event?.endTime?.let { formatDate(it) }}"
        binding.tvWaktu.text = "⏰ ${event?.beginTime?.let { formatTime(it) }} - ${event?.endTime?.let { formatTime(it) }}"
        binding.tvDeskripsi.text = HtmlCompat.fromHtml(event?.description!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
        Glide.with(this)
            .load(event?.mediaCover)
            .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
            .into(binding.ivFoto)
        binding.tvKota.text = "📍 ${event?.cityName}"
        binding.tvQuota.text = "👥 ${event?.registrants}/${event?.quota}"
        binding.tvSummary.text = event?.summary
        binding.tvOwner.text = "By: ${event?.ownerName}"
        binding.btnRegister.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event?.link))
            startActivity(intent)
        }
//        binding.fabAdd.setOnClickListener {
//            event?.let {
//                val favorite = !it.isFavorite
//                it.isFavorite = favorite
//                viewModel.updateEventFavorite(it.id, favorite)
//                updateFavoriteButton(favorite)
//            }
//        }

    }
//    private fun updateFavoriteButton(isFavorite: Boolean) {
//        val icon = if (isFavorite) R.drawable.ic_love else R.drawable.ic_love_change
//        binding.fabAdd.setImageResource(icon)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun formatDate(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        return try {
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate!!)
        } catch (e: Exception) {
            date
        }
    }
    private fun formatTime(dateTimeString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return try {
            val date = inputFormat.parse(dateTimeString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateTimeString
        }
    }
}