package com.dicoding.areunemia.view.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.remote.response.Question
import com.dicoding.areunemia.databinding.ItemAnswerHistoryBinding
import com.dicoding.areunemia.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAnswerAdapter : ListAdapter<Question, HistoryAnswerAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemAnswerHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val answer = getItem(position)
        holder.bind(answer, holder.itemView.context)
    }

    class MyViewHolder(val binding: ItemAnswerHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(question: Question, context: Context) {
            binding.tvItemTitle.text = context.getString(R.string.question_title, question.no)
            val questionsArray = context.resources.getStringArray(R.array.question_array)
            binding.tvItemQuestion.text = questionsArray[question.no - 1]

            if (question.answer == "Yes") {
                binding.tvItemAnswer.text = context.getString(R.string.yes)
                binding.tvItemAnswer.setBackgroundResource(R.drawable.rounded_bg_green)
            } else if (question.answer == "No") {
                binding.tvItemAnswer.text = context.getString(R.string.no)
                binding.tvItemAnswer.setBackgroundResource(R.drawable.rounded_bg_orange)
            }
        }

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Question>() {
            override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
                return oldItem == newItem
            }
        }
    }
}