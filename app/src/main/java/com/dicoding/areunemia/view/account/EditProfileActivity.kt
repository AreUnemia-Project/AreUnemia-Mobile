package com.dicoding.areunemia.view.account

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.local.pref.UserModel
import com.dicoding.areunemia.databinding.ActivityEditProfileBinding
import com.dicoding.areunemia.utils.showErrorDialog
import com.dicoding.areunemia.utils.showSuccessDialog
import com.dicoding.areunemia.utils.showConfirmationDialog
import com.dicoding.areunemia.view.ViewModelFactory
import java.util.Calendar
import java.util.Locale

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private var selectedGender: String = ""

    private val editProfileViewModel by viewModels<EditProfileViewModel> {
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
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
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

        binding.btnSubmitData.setOnClickListener {
            showConfirmationDialog(this, getString(R.string.confirm_submit_data)) {
                submitUserData()
            }
        }

        binding.btnSubmitPassword.setOnClickListener {
            showConfirmationDialog(this, getString(R.string.confirm_submit_password)) {
                submitPassword()
            }
        }
    }

    private fun submitUserData() {
        val name = binding.edEditName.text.toString()
        val birthDate = binding.edEditBirthDate.text.toString()

        val englishGender = genderMapping[selectedGender] ?: selectedGender
        editProfileViewModel.updateUserData(name, birthDate, englishGender, this)
    }

    private fun submitPassword() {
        val oldPassword = binding.edEditOldPassword.text.toString()
        val password = binding.edEditNewPassword.text.toString()
        val confirmPassword = binding.edEditConfirmPassword.text.toString()

        if (password.length < 8) {
            binding.edEditNewPassword.error = getString(R.string.password_error_message)
            return
        }

        if (password == confirmPassword) {
            editProfileViewModel.updatePassword(oldPassword, password, confirmPassword, this)
        } else {
            binding.edEditConfirmPassword.error = getString(R.string.confirm_password_error_message)
        }
    }

    private fun setupBirthDatePicker() {
        binding.edEditBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    binding.edEditBirthDate.setText(
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
            calendar.set(Calendar.YEAR, year - 13)
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            datePickerDialog.show()
        }
    }

    private fun setGenderSelection(gender: String) {
        val genderArray = resources.getStringArray(R.array.gender_array)
        val englishGender = genderMapping[gender] ?: gender
        val localizedGender = getLocalizedGender(englishGender)
        val position = genderArray.indexOf(localizedGender)
        if (position >= 0) {
            binding.edEditGender.setText(genderArray[position], false)
        }
    }

    private fun getLocalizedGender(gender: String): String {
        val genderArray = resources.getStringArray(R.array.gender_array)
        return when (gender.lowercase(Locale.getDefault())) {
            "male" -> genderArray[0]
            "female" -> genderArray[1]
            else -> gender
        }
    }

    private fun setupGenderDropdown() {
        val genderArray = resources.getStringArray(R.array.gender_array)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            genderArray
        )
        binding.edEditGender.setAdapter(adapter)
        binding.edEditGender.setOnItemClickListener { parent, _, position, _ ->
            selectedGender = parent.getItemAtPosition(position).toString()
        }
    }

    private fun observeViewModel() {
        var currentUser: UserModel? = null

        // Observe session data
        editProfileViewModel.getSession().observe(this) { user ->
            user?.let {
                currentUser = it
                // Set user data to the UI elements if needed
                binding.edEditName.setText(it.name)
                binding.edEditBirthDate.setText(it.birthDate)
                selectedGender = it.gender
                setGenderSelection(selectedGender)
            }
        }

        // Observe user data update result
        editProfileViewModel.editUserDataResult.observe(this) { response ->
            response?.let {
                if (it.status == "success") {
                    val message = getString(R.string.update_data_message)
                    currentUser?.let { user ->
                        val updatedUser = UserModel(
                            name = binding.edEditName.text.toString(),
                            email = user.email,
                            birthDate = binding.edEditBirthDate.text.toString(),
                            gender = binding.edEditGender.text.toString(),
                            token = user.token,
                            isLogin = true
                        )
                        editProfileViewModel.saveSession(updatedUser)
                    }
                    showSuccessDialog(this, message) {
                        finish()
                    }
                } else {
                    val errorMessage = it.message ?: getString(R.string.error_message)
                    showErrorDialog(this, errorMessage)
                }
            }
        }

        editProfileViewModel.editPasswordResult.observe(this) { response ->
            response?.let {
                if (it.status == "success") {
                    val message = getString(R.string.update_password_message)
                    showSuccessDialog(this, message) {
                        finish()
                    }
                } else {
                    val errorMessage = it.message ?: getString(R.string.error_message)
                    showErrorDialog(this, errorMessage)
                }
            }
        }

        editProfileViewModel.error.observe(this) { message ->
            message?.let {
                val errorMessage = when (it) {
                    "Please Complete the Data!" -> getString(R.string.please_complete_data)
                    "Failed to Update user" -> getString(R.string.failed_register)
                    else -> it
                }
                showErrorDialog(this, errorMessage)
            }
        }

        editProfileViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    // Other methods
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
