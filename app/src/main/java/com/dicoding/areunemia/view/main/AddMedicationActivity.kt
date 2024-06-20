package com.dicoding.areunemia.view.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.ActivityAddMedicationBinding
import com.dicoding.areunemia.utils.showErrorDialog
import com.dicoding.areunemia.utils.showSuccessDialog
import com.dicoding.areunemia.utils.showToast
import com.dicoding.areunemia.view.ViewModelFactory
import java.util.*

class AddMedicationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMedicationBinding
    private val addMedicationViewModel by viewModels<AddMedicationViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMedicationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        observeViewModel()

        val selectedDate = intent.getStringExtra("SELECTED_DATE")
        selectedDate?.let {
            binding.edEditStartDate.setText(it)
        }
    }

    private fun setupAction() {
        binding.edEditStartDate.setOnClickListener {
            showDatePickerDialog { date ->
                binding.edEditStartDate.setText(date)
            }
        }

        addScheduleField()
        binding.btnAddSchedule.setOnClickListener {
            if (binding.scheduleContainer.childCount < 6) {
                addScheduleField()
            } else {
                showToast(this, getString(R.string.maximum_field_schedule))
            }
        }

        binding.btnSubmitData.setOnClickListener {
            submitData()
        }
    }

    private fun showDatePickerDialog(onDateSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = getString(
                    R.string.date_format,
                    selectedYear,
                    selectedMonth + 1,
                    selectedDay
                )
                onDateSet(date)
            },
            year, month, day
        )

        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun showTimePickerDialog(onTimeSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val time = getString(R.string.time_format, selectedHour, selectedMinute)
                onTimeSet(time)
            },
            hour, minute, true
        )

        timePickerDialog.show()
    }

    private fun addScheduleField() {
        val inflater = LayoutInflater.from(this)
        val scheduleLayout: View = inflater.inflate(R.layout.field_schedule_input, binding.scheduleContainer, false)

        val edScheduleTime = scheduleLayout.findViewById<EditText>(R.id.ed_schedule_time)
        edScheduleTime.setOnClickListener {
            showTimePickerDialog { time ->
                edScheduleTime.setText(time)
            }
        }

        val btnDeleteSchedule = scheduleLayout.findViewById<ImageView>(R.id.btnDeleteSchedule)
        btnDeleteSchedule?.setOnClickListener {
            binding.scheduleContainer.removeView(scheduleLayout)
            updateDeleteButtons()
        }

        binding.scheduleContainer.addView(scheduleLayout)
        updateDeleteButtons()
    }

    private fun updateDeleteButtons() {
        for (i in 0 until binding.scheduleContainer.childCount) {
            val scheduleLayout = binding.scheduleContainer.getChildAt(i)
            val btnDeleteSchedule = scheduleLayout.findViewById<ImageView>(R.id.btnDeleteSchedule)
            btnDeleteSchedule?.isEnabled = binding.scheduleContainer.childCount > 2
        }
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    private fun submitData() {
        val medicationName = binding.edEditName.text.toString().trim()
        val dosage = binding.edEditDosage.text.toString().trim()
        val startDate = binding.edEditStartDate.text.toString().trim()
        val endDate = binding.edEditStartDate.text.toString().trim()
        val notes = binding.edEditNotes.text.toString().trim()

        val scheduleList = mutableListOf<String>()
        for (i in 0 until binding.scheduleContainer.childCount) {
            val scheduleLayout = binding.scheduleContainer.getChildAt(i)
            val edScheduleTime = scheduleLayout.findViewById<EditText>(R.id.ed_schedule_time)
            val scheduleTime = edScheduleTime?.text?.toString()?.trim()
            if (!scheduleTime.isNullOrBlank()) {
                scheduleList.add(scheduleTime)
            }
        }

        if (medicationName.isEmpty() || dosage.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || scheduleList.isEmpty()) {
            showToast(this, getString(R.string.please_complete_data))
            return
        }
        val schedules = scheduleList.joinToString(",")
        addMedicationViewModel.addNewMedication(medicationName, dosage, startDate, endDate, schedules, notes, this)

    }

    private fun observeViewModel() {
        addMedicationViewModel.addMedicationResult.observe(this) { response ->
            response?.let {
                if (it.status == "success") {
                    val message = getString(R.string.medication_added_message)
                    showSuccessDialog(this, message) {
                        finish()
                    }
                } else {
                    val errorMessage = it.message ?: getString(R.string.error_message)
                    showErrorDialog(this, errorMessage)
                }
            }
        }


        addMedicationViewModel.error.observe(this) { message ->
            message?.let {
                val errorMessage = when (it) {
                    "Medication already exists!" -> getString(R.string.medication_duplicate_message)
                    "Please complete the required data" -> getString(R.string.please_complete_data)
                    "Failed to add medication" -> getString(R.string.failed_add_medication)
                    else -> it
                }
                showErrorDialog(this, errorMessage)
            }
        }

        addMedicationViewModel.isLoading.observe(this) {
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
