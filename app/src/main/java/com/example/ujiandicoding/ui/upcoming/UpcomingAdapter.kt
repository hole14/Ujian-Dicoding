package com.example.ujiandicoding.ui.upcoming

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ujiandicoding.R
import com.example.ujiandicoding.data.entity.EventEntity
import com.example.ujiandicoding.databinding.ItemUpcomingBinding
import com.example.ujiandicoding.ui.detail.DetailActivity
import java.text.SimpleDateFormat
import java.util.Locale

class UpcomingAdapter(private val onFavoriteClick: (EventEntity) -> Unit): ListAdapter<EventEntity, UpcomingAdapter.ViewHolder>(DIFF_CALLBACK) {
    class ViewHolder(val binding: ItemUpcomingBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(event: EventEntity) {
            binding.tvJudul.text = event.name
            binding.tvKategori.text = event.category
            binding.tvTanggal.text = "${event.beginTime.let { formatDate(it) }} - ${event.endTime.let { formatDate(it) }}"
            Glide.with(itemView.context)
                .load(event.mediaCover)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
                .into(binding.ivFoto)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_EVENT, event)
                itemView.context.startActivity(intent)
            }
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
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
        val ivFavorite = holder.binding.ivFavorite
        if (event.isFavorite) {
            ivFavorite.setImageDrawable(ContextCompat.getDrawable(ivFavorite.context, R.drawable.ic_love_change))
        } else {
            ivFavorite.setImageDrawable(ContextCompat.getDrawable(ivFavorite.context, R.drawable.ic_love))
        }
        ivFavorite.setOnClickListener {
            onFavoriteClick(event)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUpcomingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<EventEntity> =
            object : DiffUtil.ItemCallback<EventEntity>() {
                override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}