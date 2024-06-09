package com.dicoding.areunemia.view.history

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.remote.response.HistoryItem
import com.dicoding.areunemia.databinding.ActivityHistoryBinding
import com.dicoding.areunemia.utils.navigateToOtherFeature
import com.dicoding.areunemia.utils.showLoginAlertDialog
import com.dicoding.areunemia.view.ViewModelFactory
import com.dicoding.areunemia.view.account.AccountActivity
import com.dicoding.areunemia.view.adapter.HistoryAdapter
import com.dicoding.areunemia.view.main.MainActivity
import com.dicoding.areunemia.view.scan.ScanActivity

class HistoryActivity : AppCompatActivity() {

    private val historyViewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.selectedItemId = R.id.bottom_history
        setupBottomNavigation()
        setupView()

        historyViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                showLoginAlertDialog(this)
            } else {
                setupViewLoggedIn()
                observeViewModel()
            }
        }
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun setupViewLoggedIn() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvHistoryResult.layoutManager = layoutManager
        historyViewModel.getListHistories()
    }

    private fun observeViewModel() {
        historyViewModel.listHistory.observe(this) { historyResult ->
            setHistoryResults(historyResult)
        }

        historyViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun setHistoryResults(historyResult: List<HistoryItem?>?) {
        val adapter = HistoryAdapter()
        adapter.submitList(historyResult)
        binding.rvHistoryResult.adapter = adapter
        setupAction(adapter)
    }

    private fun setupAction(adapter: HistoryAdapter) {
        adapter.setOnItemClickListener(object : HistoryAdapter.OnItemClickListener {
            override fun onItemClick(history: HistoryItem) {
                val intent = Intent(this@HistoryActivity, HistoryDetailActivity::class.java)
                intent.putExtra("prediction_id", history.predictionId)
                startActivity(intent)
            }
        })
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    navigateToOtherFeature(this, MainActivity::class.java)
                    true
                }
                R.id.bottom_scan -> {
                    navigateToOtherFeature(this, ScanActivity::class.java)
                    true
                }
                R.id.bottom_history -> true
                R.id.bottom_account -> {
                    navigateToOtherFeature(this, AccountActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }
}