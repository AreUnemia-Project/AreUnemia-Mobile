package com.dicoding.areunemia.view.history

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.remote.response.HistoryItem
import com.dicoding.areunemia.databinding.ActivityHistoryBinding
import com.dicoding.areunemia.view.ViewModelFactory
import com.dicoding.areunemia.view.account.AccountActivity
import com.dicoding.areunemia.view.adapter.HistoryAdapter
import com.dicoding.areunemia.view.login.LoginActivity
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
                showLoginAlertDialog()
                setupAction()
                observeViewModel()
            } else {
                // User is logged in
                // show history
            }
        }
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun setupAction() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvHistoryResult.layoutManager = layoutManager

        historyViewModel.getListStories()
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

    private fun showLoginAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.login_required)
        builder.setMessage(R.string.need_to_login)
        builder.setPositiveButton(R.string.yes) { _, _ ->
            startActivity(Intent(this, LoginActivity::class.java))
        }
        builder.setNegativeButton(R.string.no) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    navigateToActivity(MainActivity::class.java)
                    true
                }
                R.id.bottom_scan -> {
                    navigateToActivity(ScanActivity::class.java)
                    true
                }
                R.id.bottom_history -> true
                R.id.bottom_account -> {
                    navigateToActivity(AccountActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        startActivity(Intent(applicationContext, activityClass))
        overridePendingTransition(0, 0)
        finish()
    }
}