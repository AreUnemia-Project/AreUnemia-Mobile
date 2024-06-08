package com.dicoding.areunemia.view.login

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.local.pref.UserModel
import com.dicoding.areunemia.databinding.ActivityLoginBinding
import com.dicoding.areunemia.utils.*
import com.dicoding.areunemia.view.ViewModelFactory
import com.dicoding.areunemia.view.main.MainActivity
import com.dicoding.areunemia.view.register.RegisterActivity


class LoginActivity : AppCompatActivity() {

    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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
        val registerHereText = getString(R.string.register_here)
        val fullText = getString(R.string.go_to_register, registerHereText)
        val spannableString = SpannableString(fullText)
        val start = fullText.indexOf(registerHereText)
        val end = start + registerHereText.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                finish()
            }
        }

        spannableString.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.messageTextView.text = spannableString
        binding.messageTextView.movementMethod = LinkMovementMethod.getInstance()
        binding.messageTextView.alpha = 1f

        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            loginViewModel.loginUser(email, password, this)
        }
    }

    private fun observeViewModel() {
        loginViewModel.loginResult.observe(this) { response ->
            response?.let {
                if (it.status == "success") {
                    val user = it.data?.let { loginResult ->
                        it.token?.let { it1 ->
                            UserModel(
                                loginResult.name,
                                loginResult.email,
                                loginResult.birthdate,
                                loginResult.gender,
                                it1,
                            )
                        }
                    }
                    user?.let { userModel ->
                        loginViewModel.saveSession(userModel)
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }

        loginViewModel.error.observe(this) { message ->
            message?.let {
                showErrorDialog(this, it)
            }
        }

        loginViewModel.isLoading.observe(this) {
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