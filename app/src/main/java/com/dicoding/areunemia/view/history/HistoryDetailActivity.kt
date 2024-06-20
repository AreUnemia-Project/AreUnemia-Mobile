package com.dicoding.areunemia.view.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.remote.response.HistoryDetailItem
import com.dicoding.areunemia.data.remote.response.toQuestionList
import com.dicoding.areunemia.databinding.ActivityHistoryDetailBinding
import com.dicoding.areunemia.view.ViewModelFactory
import com.dicoding.areunemia.view.adapter.AnswerHistoryAdapter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

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

        historyDetailViewModel.getHistoryDetail(id, this)

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

    private fun setHistoryDetailResults(historyDetailResult: HistoryDetailItem) {
        val (textResId, descResId, colorResId) = when (historyDetailResult.predictionResult) {
            "Severe" -> Triple(R.string.severe, R.string.severe_desc, R.color.red_dark)
            "Moderate" -> Triple(R.string.moderate, R.string.moderate_desc, R.color.red)
            "Mild" -> Triple(R.string.mild, R.string.mild_desc, R.color.orange)
            "Healthy" -> Triple(R.string.no_anemia, R.string.healthy_desc, R.color.grey_dark)
            else -> Triple(R.string.unknown_prediction, R.string.healthy_desc, R.color.grey_dark)
        }

        binding.tvHistoryDetailResult.text = getString(textResId)
        binding.tvHistoryDetailResult.setTextColor(ContextCompat.getColor(this, colorResId))
        binding.tvPredictionDesc.text = getString(descResId)

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
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val date = sdf.parse(dateString)

                val newSdf = SimpleDateFormat("EEEE • dd MMMM yyyy • HH:mm:ss", Locale.getDefault())
                newSdf.timeZone = TimeZone.getTimeZone("Asia/Jakarta") // WIB timezone

                return date?.let { newSdf.format(it) }
            } catch (e: ParseException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
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