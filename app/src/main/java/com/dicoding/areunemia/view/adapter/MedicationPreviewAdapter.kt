package com.dicoding.areunemia.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.remote.response.MedicationData
import com.dicoding.areunemia.databinding.ItemMedicationPreviewBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MedicationPreviewAdapter : ListAdapter<MedicationData, MedicationPreviewAdapter.MyViewHolder>(MedicationPreviewAdapter.DIFF_CALLBACK) {
    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(medication: MedicationData)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemMedicationPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicationPreviewAdapter.MyViewHolder, position: Int) {
        val med = getItem(position)
        holder.bind(med, holder.itemView.context)
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(med)
        }
    }

    class MyViewHolder(val binding: ItemMedicationPreviewBinding) : RecyclerView.ViewHolder(binding.root) {
        private val inputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        private val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        private val today: Date = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        fun bind(medication: MedicationData, context: Context) {
            val formattedDate = formatDate(context, medication.startDate, medication.schedule)
            binding.tvItemDate.text = formattedDate
            binding.tvItemName.text = medication.medicationName
            binding.tvItemDescription.text = if (medication.notes.isEmpty()) {
                context.getString(R.string.medication_desc_initial, medication.dosage)
            } else {
                context.getString(R.string.medication_desc, medication.dosage, medication.notes)
            }
            Glide.with(context)
                .load(getPillImageResource(context, formattedDate))
                .into(binding.ivPill)
        }

        private fun formatDate(context: Context, dateString: String?, timeString: String?): String {
            return dateString?.let {
                try {
                    val date = inputFormat.parse(it)
                    val formattedDate = if (date == today) {
                        context.getString(R.string.today)
                    } else {
                        outputFormat.format(date)
                    }
                    if (!timeString.isNullOrEmpty()) {
                        context.getString(R.string.formatted_date_with_time, formattedDate, timeString)
                    } else {
                        formattedDate
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    ""
                }
            } ?: ""
        }

        private fun getPillImageResource(context: Context, formattedDate: String): Int {
            return if (formattedDate.contains(context.getString(R.string.today))) {
                R.drawable.pic_pill_today
            } else {
                R.drawable.pic_pill_other
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MedicationData>() {
            override fun areItemsTheSame(oldItem: MedicationData, newItem: MedicationData): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: MedicationData, newItem: MedicationData): Boolean {
                return oldItem == newItem
            }
        }
    }

}