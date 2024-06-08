package com.dicoding.areunemia.view.account

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.ActivityAccountBinding
import com.dicoding.areunemia.utils.navigateToOtherFeature
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
                    navigateToOtherFeature(this, MainActivity::class.java)
                    true
                }
                R.id.bottom_scan -> {
                    navigateToOtherFeature(this, ScanActivity::class.java)
                    true
                }
                R.id.bottom_history -> {
                    navigateToOtherFeature(this, HistoryActivity::class.java)
                    true
                }
                R.id.bottom_account -> true
                else -> false
            }
        }
    }

}