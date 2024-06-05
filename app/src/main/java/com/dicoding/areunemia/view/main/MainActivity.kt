package com.dicoding.areunemia.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.ActivityMainBinding
import com.dicoding.areunemia.view.ViewModelFactory
import com.dicoding.areunemia.view.account.AccountActivity
import com.dicoding.areunemia.view.history.HistoryActivity
import com.dicoding.areunemia.view.login.LoginActivity
import com.dicoding.areunemia.view.scan.ScanActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.selectedItemId = R.id.bottom_home
        setupBottomNavigation()
        setupView()

    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> true
                R.id.bottom_scan -> {
                    navigateToActivity(ScanActivity::class.java)
                    true
                }
                R.id.bottom_history -> {
                    navigateToActivity(HistoryActivity::class.java)
                    true
                }
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