package com.dicoding.areunemia.view.scan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.ActivityScanBinding
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
                showLoginAlertDialog()
            } else {
                // User is logged in
                // continue to next page
                navigateToActivity(ScanProcessActivity::class.java)
            }
        }
    }

    private fun showLoginAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.login_required)
        builder.setMessage(R.string.need_to_login)
        builder.setPositiveButton(R.string.yes) { _, _ ->
            startActivity(Intent(this, ScanProcessActivity::class.java))
//            startActivity(Intent(this, LoginActivity::class.java))
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
                R.id.bottom_scan -> true
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
        finish()
    }

}