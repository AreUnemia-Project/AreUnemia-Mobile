package com.dicoding.areunemia.view.register

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.ActivityRegisterBinding
import com.dicoding.areunemia.utils.*
import com.dicoding.areunemia.view.ViewModelFactory
import com.dicoding.areunemia.view.login.LoginActivity
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var selectedGender: String = ""
    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val genderMapping = mapOf(
        "Male" to "Male",
        "Female" to "Female",
        "Pria" to "Male",
        "Wanita" to "Female"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        observeViewModel()
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    private fun setupAction() {

        setupBirthDatePicker()
        setupGenderDropdown()

        val loginHereText = getString(R.string.login_here)
        val fullText = getString(R.string.go_to_login, loginHereText)
        val spannableString = SpannableString(fullText)
        val start = fullText.indexOf(loginHereText)
        val end = start + loginHereText.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }

        spannableString.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.messageTextView.text = spannableString
        binding.messageTextView.movementMethod = LinkMovementMethod.getInstance()
        binding.messageTextView.alpha = 1f

        binding.registerButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val birthDate = binding.edRegisterBirthDate.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val confirmPassword = binding.edLoginConfirmPassword.text.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edRegisterEmail.error = getString(R.string.email_error_message)
                return@setOnClickListener
            }

            if (password.length < 8) {
                binding.edRegisterPassword.error = getString(R.string.password_error_message)
                return@setOnClickListener
            }

            if (password == confirmPassword) {
                val englishGender = genderMapping[selectedGender] ?: selectedGender
                registerViewModel.registerUser(name, birthDate, englishGender, email, password, this)
            } else {
                binding.edLoginConfirmPassword.error = getString(R.string.confirm_password_error_message)
            }
        }
    }

    private fun setupBirthDatePicker() {
        binding.edRegisterBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    binding.edRegisterBirthDate.setText(
                        getString(
                            R.string.date_format,
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay
                        )
                    )
                },
                year, month, day
            )
            calendar.set(Calendar.YEAR, year - 12)
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            datePickerDialog.show()
        }
    }


    private fun setupGenderDropdown() {
        val genderArray = resources.getStringArray(R.array.gender_array)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            genderArray
        )
        binding.edRegisterGender.setAdapter(adapter)
        binding.edRegisterGender.setOnItemClickListener { parent, view, position, id ->
            selectedGender = parent.getItemAtPosition(position).toString()
        }
    }

    private fun observeViewModel() {
        registerViewModel.registerResult.observe(this) { response ->
            response?.let {
                if (it.status == "success") {
                    val message = getString(R.string.signup_success_message, binding.edRegisterEmail.text.toString())
                    showSuccessDialog(this, message) {
                        finish()
                    }
                }
            }
        }

        registerViewModel.error.observe(this) { message ->
            message?.let {
                showErrorDialog(this, it)
            }
        }

        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }
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