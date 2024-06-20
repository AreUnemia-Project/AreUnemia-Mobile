package com.dicoding.areunemia.view.account

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.local.pref.UserModel
import com.dicoding.areunemia.databinding.ActivityAccountBinding
import com.dicoding.areunemia.utils.navigateToOtherFeature
import com.dicoding.areunemia.view.ViewModelFactory
import com.dicoding.areunemia.view.history.HistoryActivity
import com.dicoding.areunemia.view.login.LoginActivity
import com.dicoding.areunemia.view.main.MainActivity
import com.dicoding.areunemia.view.scan.ScanActivity
import java.text.SimpleDateFormat
import java.util.Locale

class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding
    private val accountViewModel by viewModels<AccountViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.selectedItemId = R.id.bottom_account
        setupBottomNavigation()
        setupView()
        setupAction()

        accountViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                // If not logged in
                binding.profileSection.visibility = View.GONE
                binding.buttonEditProfile.visibility = View.GONE
                binding.buttonLogout.visibility = View.GONE
                binding.profileSection2.visibility = View.VISIBLE
            } else {
                // If logged in
                setupViewLoggedIn(user)
                binding.profileSection.visibility = View.VISIBLE
                binding.buttonEditProfile.visibility = View.VISIBLE
                binding.buttonLogout.visibility = View.VISIBLE
                binding.profileSection2.visibility = View.GONE
            }
        }

    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.apply {
            loginButton.setOnClickListener {
                startActivity(Intent(this@AccountActivity, LoginActivity::class.java))
            }
            buttonEditProfile.setOnClickListener {
                val intent = Intent(this@AccountActivity, EditProfileActivity::class.java)
                startActivity(intent)
            }
            buttonLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            buttonLogout.setOnClickListener {
                val builder = AlertDialog.Builder(this@AccountActivity)
                builder.setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.are_you_sure_logout))
                    .setPositiveButton(getString(R.string.logout)) { _, _ ->
                        // Handle logout logic (e.g., remove user data, navigate to login)
                        accountViewModel.logout()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                builder.show()
            }
        }
    }

    private fun setupViewLoggedIn(user: UserModel) {
        binding.apply {
            tvName.text = user.name
            tvEmail.text = user.email
            tvBirthdate.text = formatDate(user.birthDate)
            tvGender.text = getLocalizedGender(user.gender)
        }
    }

    private fun getLocalizedGender(gender: String): String {
        val genderArray = resources.getStringArray(R.array.gender_array)
        return when (gender.lowercase(Locale.getDefault())) {
            "male", "pria" -> genderArray[0]
            "female", "wanita" -> genderArray[1]
            else -> gender
        }
    }

    private fun formatDate(dateString: String?): String? {
        dateString?.let {
            return try {
                val inputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                val date = inputFormat.parse(it)
                outputFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
        return ""
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
