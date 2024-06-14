package com.dicoding.areunemia.view.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.remote.response.HistoryItem
import com.dicoding.areunemia.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter: ListAdapter<HistoryItem, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(history: HistoryItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history, holder.itemView.context)
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(history)
        }
    }

    class MyViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryItem, context: Context) {
            binding.tvItemDate.text = formatDate(history.createdAt)
            binding.tvItemName.text = formatPredictionResult(history.predictionResult, context)
        }

        private fun formatDate(dateString: String?): String? {
            dateString?.let {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val date = sdf.parse(dateString)
                val newSdf = SimpleDateFormat("EEEE • dd MMMM yyyy", Locale.getDefault())
                return date?.let { it1 -> newSdf.format(it1) }
            }
            return ""
        }

        private fun formatPredictionResult(predictionResult: String?, context: Context): SpannableString {
            val startIndex = if (Locale.getDefault().language == "in") 17 else 18
            return when (predictionResult) {
                "Severe" -> {
                    val formattedText = context.getString(R.string.anemia_prediction_template,
                        context.getString(R.string.severe))
                    val spannableString = SpannableString(formattedText)
                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.red_dark)),
                        startIndex,
                        formattedText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString
                }
                "Moderate" -> {
                    val formattedText = context.getString(R.string.anemia_prediction_template,
                        context.getString(R.string.moderate))
                    val spannableString = SpannableString(formattedText)
                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)),
                        startIndex,
                        formattedText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString
                }
                "Mild" -> {
                    val formattedText = context.getString(R.string.anemia_prediction_template,
                        context.getString(R.string.mild))
                    val spannableString = SpannableString(formattedText)
                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.orange)),
                        startIndex,
                        formattedText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString
                }
                "Healthy" -> {
                    val formattedText = context.getString(R.string.no_anemia_predicted)
                    val spannableString = SpannableString(formattedText)
                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.grey_dark)),
                        0,
                        formattedText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString
                }
                else -> {
                    val formattedText = context.getString(R.string.unknown_prediction)
                    val spannableString = SpannableString(formattedText)
                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.grey_dark)),
                        0,
                        formattedText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryItem>() {
            override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}