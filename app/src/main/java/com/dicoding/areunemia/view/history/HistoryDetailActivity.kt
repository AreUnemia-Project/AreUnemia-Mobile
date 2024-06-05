package com.dicoding.areunemia.view.history

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.dicoding.areunemia.data.remote.response.PredictionItem
import com.dicoding.areunemia.databinding.ActivityHistoryDetailBinding
import com.dicoding.areunemia.view.ViewModelFactory
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

        setupView()

        historyDetailViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                showLoginAlertDialog()
                predictionId?.let { predictionId ->
                    setupAction(predictionId)
                }
                observeViewModel()
            } else {
                // User is logged in
                // show history detail
            }
        }
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    private fun setupAction(id: String) {
        historyDetailViewModel.getHistoryDetail(id)
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
        binding.tvHistoryDetailResult.text = historyDetailResult.predictionResult
        binding.tvHistoryDetailDate.text = formatDate(historyDetailResult.createdAt)
        Glide.with(this@HistoryDetailActivity)
            .load(historyDetailResult.eyePhoto)
            .into(binding.ivEyesPhoto)
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

    private fun showLoginAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Login Required")
        builder.setMessage("You need to log in to access this feature. Do you want to log in now?")
        builder.setPositiveButton("Yes") { _, _ ->
            startActivity(Intent(this, LoginActivity::class.java))
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
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