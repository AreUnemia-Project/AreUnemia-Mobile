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
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.ActivityRegisterBinding
import com.dicoding.areunemia.view.login.LoginActivity
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupBirthDatePicker()
        setupGenderDropdown()
        setupAction()
    }

    private fun setupView() {// Enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    private fun setupAction() {
        val loginHereText = getString(R.string.login_here)
        val fullText = getString(R.string.go_to_login, loginHereText)
        val spannableString = SpannableString(fullText)
        val start = fullText.indexOf(loginHereText)
        val end = start + loginHereText.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }
        }

        spannableString.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.messageTextView.text = spannableString
        binding.messageTextView.movementMethod = LinkMovementMethod.getInstance()
        binding.messageTextView.alpha = 1f
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
            // Handle gender selection if needed
        }
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