package com.dicoding.areunemia.view.history

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.remote.response.PredictionItem
import com.dicoding.areunemia.data.remote.response.toQuestionList
import com.dicoding.areunemia.databinding.ActivityHistoryDetailBinding
import com.dicoding.areunemia.view.ViewModelFactory
import com.dicoding.areunemia.view.adapter.AnswerHistoryAdapter
import com.dicoding.areunemia.view.login.LoginActivity
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryDetailActivity : AppCompatActivity() {

    private val historyDetailViewModel by viewModels<HistoryDetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityHistoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val predictionId = intent.getStringExtra("prediction_id")

        predictionId?.let { predictionId ->
            setupView(predictionId)
        }
        observeViewModel()
    }

    private fun setupView(id: String) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        historyDetailViewModel.getHistoryDetail(id)
        val layoutManager = LinearLayoutManager(this)
        binding.rvQuestionnaireResponse.layoutManager = layoutManager
    }


    private fun observeViewModel() {
        historyDetailViewModel.historyDetail.observe(this) { historyDetailResult ->
            setHistoryDetailResults(historyDetailResult)
        }

        historyDetailViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun setHistoryDetailResults(historyDetailResult: PredictionItem) {
        val (textResId, colorResId) = when (historyDetailResult.predictionResult) {
            "Severe" -> R.string.severe to R.color.red_dark
            "Moderate" -> R.string.moderate to R.color.red
            "Mild" -> R.string.mild to R.color.orange
            "Healthy" -> R.string.no_anemia to R.color.grey_dark
            else -> R.string.unknown_prediction to R.color.grey_dark
        }

        binding.tvHistoryDetailResult.text = textResId?.let { getString(it) } ?: historyDetailResult.predictionResult
        binding.tvHistoryDetailResult.setTextColor(ContextCompat.getColor(this, colorResId))

        binding.tvHistoryDetailDate.text = formatDate(historyDetailResult.createdAt)
        Glide.with(this@HistoryDetailActivity)
            .load(historyDetailResult.eyePhoto)
            .into(binding.ivEyesPhoto)

        val questionsList = historyDetailResult.questionnaireAnswers.toQuestionList()

        val adapter = AnswerHistoryAdapter()
        adapter.submitList(questionsList)
        binding.rvQuestionnaireResponse.adapter = adapter

    }

    private fun formatDate(dateString: String?): String? {
        dateString?.let {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = sdf.parse(dateString)
            val newSdf = SimpleDateFormat("EEEE • MMMM dd'th', yyyy", Locale.getDefault())
            return date?.let { it1 -> newSdf.format(it1) }
        }
        return ""
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}