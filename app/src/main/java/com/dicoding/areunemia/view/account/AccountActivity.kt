package com.dicoding.areunemia.view.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.ActivityAccountBinding
import com.dicoding.areunemia.view.history.HistoryActivity
import com.dicoding.areunemia.view.main.MainActivity
import com.dicoding.areunemia.view.scan.ScanActivity

class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.selectedItemId = R.id.bottom_account
        setupBottomNavigation()
        setupView()

    }

    private fun setupView() {
        supportActionBar?.hide()
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
                R.id.bottom_history -> {
                    navigateToActivity(HistoryActivity::class.java)
                    true
                }
                R.id.bottom_account -> true
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