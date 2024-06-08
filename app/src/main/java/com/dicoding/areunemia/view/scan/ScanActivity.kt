package com.dicoding.areunemia.view.scan

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.ActivityScanBinding
import com.dicoding.areunemia.utils.navigateToOtherFeature
import com.dicoding.areunemia.utils.showLoginAlertDialog
import com.dicoding.areunemia.utils.showWarningDialog
import com.dicoding.areunemia.view.ViewModelFactory
import com.dicoding.areunemia.view.account.AccountActivity
import com.dicoding.areunemia.view.history.HistoryActivity
import com.dicoding.areunemia.view.login.LoginActivity
import com.dicoding.areunemia.view.main.MainActivity

class ScanActivity : AppCompatActivity() {

    private val scanViewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(this)
    }
    
    private lateinit var binding: ActivityScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.selectedItemId = R.id.bottom_scan
        setupBottomNavigation()
        setupView()
        setupAction()
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.continueScanButton.setOnClickListener {
            observeViewModel()
        }
    }

    private fun observeViewModel() {
        scanViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                showLoginAlertDialog(this)
            } else {
                showWarningDialog(this, getString(R.string.warning_to_scan)) {
                    startActivity(Intent(this, ScanProcessActivity::class.java))
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    navigateToOtherFeature(this, MainActivity::class.java)
                    true
                }
                R.id.bottom_scan -> true
                R.id.bottom_history -> {
                    navigateToOtherFeature(this, HistoryActivity::class.java)
                    true
                }
                R.id.bottom_account -> {
                    navigateToOtherFeature(this, AccountActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }

}