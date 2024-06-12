package com.dicoding.areunemia.view.account

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.ActivityAccountBinding
import com.dicoding.areunemia.utils.navigateToOtherFeature
import com.dicoding.areunemia.view.history.HistoryActivity
import com.dicoding.areunemia.view.main.MainActivity
import com.dicoding.areunemia.view.scan.ScanActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.selectedItemId = R.id.bottom_account
        setupBottomNavigation()
        setupView()

        // Handle button clicks
        binding.button.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.button2.setOnClickListener {
            val intent = Intent(this, ThemeActivity::class.java)
            startActivity(intent)
        }

        binding.button3.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
        }

        binding.button4.setOnClickListener {
            // Show confirmation dialog before logout
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    // Handle logout logic (e.g., remove user data, navigate to login)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
            builder.show()
        }
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
