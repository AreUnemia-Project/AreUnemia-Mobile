package com.dicoding.areunemia.view.scan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.ActivityScanResultsBinding
import com.dicoding.areunemia.utils.showErrorDialog
import com.dicoding.areunemia.view.history.HistoryActivity
import com.dicoding.areunemia.view.main.MainActivity

class ScanResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanResultsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val result = intent.getStringExtra("result")

        setupView()
        if (result != null) {
            setupAction(result)
        }
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun setupAction(result: String) {
        setPredictionResult(result)
        binding.viewHistoryButton.setOnClickListener {
            navigateToActivity(HistoryActivity::class.java)
        }
        binding.backHomeButton.setOnClickListener {
            navigateToActivity(MainActivity::class.java)
        }
    }

    private fun setPredictionResult(result: String) {

        val (imageRes, severityRes, descTextRes) = when (result) {
            "Severe" -> Triple(R.drawable.pic_severe_anemia, R.string.severe, R.string.severe_desc)
            "Moderate" -> Triple(R.drawable.pic_moderate_anemia, R.string.moderate, R.string.moderate_desc)
            "Mild" -> Triple(R.drawable.pic_mild_anemia, R.string.mild, R.string.mild_desc)
            "Healthy" -> Triple(R.drawable.pic_no_anemia, R.string.no, R.string.healthy_desc)
            else -> {
                showErrorDialog(this, getString(R.string.error_message))
                return
            }
        }

        val colorRes = when (result) {
            "Severe" -> R.color.red_dark
            "Moderate" -> R.color.red
            "Mild" -> R.color.orange
            "Healthy" -> R.color.grey_dark
            else -> R.color.grey_dark
        }

        binding.ivResultsPhoto.setImageResource(imageRes)
        binding.tvPredictionResults.text = getString(R.string.anemia_text, getString(severityRes))
        binding.tvPredictionResults.setTextColor(ContextCompat.getColor(this@ScanResultsActivity, colorRes))
        binding.tvPredictionDesc.text = getString(descTextRes)

    }

    private fun navigateToActivity(activityClass: Class<*>) {
        startActivity(Intent(applicationContext, activityClass))
        finish()
    }

}