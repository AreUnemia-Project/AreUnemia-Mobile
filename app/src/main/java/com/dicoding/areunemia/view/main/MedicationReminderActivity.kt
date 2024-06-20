package com.dicoding.areunemia.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.local.pref.CalendarData
import com.dicoding.areunemia.data.remote.response.MedicationData
import com.dicoding.areunemia.databinding.ActivityMedicationReminderBinding
import com.dicoding.areunemia.utils.showConfirmationDialog
import com.dicoding.areunemia.utils.showErrorDialog
import com.dicoding.areunemia.utils.showSuccessDialog
import com.dicoding.areunemia.view.ViewModelFactory
import com.dicoding.areunemia.view.adapter.CalendarAdapter
import com.dicoding.areunemia.view.adapter.MedicationAdapter
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MedicationReminderActivity : AppCompatActivity(), CalendarAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMedicationReminderBinding
    private val medicationReminderViewModel by viewModels<MedicationReminderViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var currentCalendar: Calendar
    private var selectedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicationReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        observeViewModel()
        setupCalendarRecyclerView()
        setupMonthYearPicker()
    }

    override fun onResume() {
        super.onResume()
        medicationReminderViewModel.getListMedications(this)
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        medicationReminderViewModel.getListMedications(this)
        val layoutManager = LinearLayoutManager(this)
        binding.rvMedicationList.layoutManager = layoutManager
    }

    private fun setupAction() {
        binding.btnAddMedication.setOnClickListener {
            val intent = Intent(this@MedicationReminderActivity, AddMedicationActivity::class.java)
            selectedDate?.let {
                val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                intent.putExtra("SELECTED_DATE", dateFormat.format(it))
            }
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        medicationReminderViewModel.listMedication.observe(this) { medResult ->
            if (medResult.isNullOrEmpty()) {
                setMedicationResults(emptyList())
                showNoMedicationMessage(true)
            } else {
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                val medicationDataList = medResult
                    .filter { medItem ->
                        val startDate = parseDate(medItem.startDate)
                        startDate != null && startDate >= today && medItem.schedule.isNotEmpty()
                    }
                    .flatMap { medItem ->
                        medItem.schedule.map { schedule ->
                            MedicationData(
                                medicationName = medItem.medicationName,
                                dosage = medItem.dosage,
                                schedule = schedule,
                                createdAt = medItem.createdAt,
                                notes = medItem.notes,
                                endDate = medItem.endDate,
                                id = medItem.id,
                                startDate = medItem.startDate
                            )
                        }
                    }
                    .sortedWith(compareBy(
                        { parseDate(it.startDate) },
                        { parseTime(it.schedule) }
                    ))

                if (medicationDataList.isEmpty()) {
                    setMedicationResults(emptyList())
                    showNoMedicationMessage(true)
                } else {
                    setMedicationResults(medicationDataList)
                    showNoMedicationMessage(false)
                }
            }
        }

        medicationReminderViewModel.error.observe(this) { message ->
            message?.let {
                if (it == "No medications found") {
                    showNoMedicationMessage(true)
                } else {
                    val errorMessage = when (it) {
                        "User ID is required" -> getString(R.string.user_id_required)
                        "Error getting medications" -> getString(R.string.failed_get_medication)
                        "Please complete the data" -> getString(R.string.please_complete_data)
                        "Error deleting medications" -> getString(R.string.failed_delete_medication)
                        "Please provide the required data" -> getString(R.string.please_complete_data)
                        "Medication not found" -> getString(R.string.medication_not_found)
                        "Failed to update medication schedule" -> getString(R.string.failed_update)
                        else -> it
                    }
                    showErrorDialog(this, errorMessage)
                }
            }
        }

        medicationReminderViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        medicationReminderViewModel.deleteMedicationResult.observe(this) { response ->
            response?.let {
                if (it.status == "success") {
                    showSuccessDialog(this, getString(R.string.medication_deleted)) {
                        medicationReminderViewModel.getListMedications(this)
                    }
                } else {
                    showErrorDialog(this, it.message ?: getString(R.string.error_message))
                }
            }
        }

        medicationReminderViewModel.skipMedicationResult.observe(this) { response ->
            response?.let {
                if (it.status == "success") {
                    showSuccessDialog(this, getString(R.string.medication_skipped)) {
                        medicationReminderViewModel.getListMedications(this)
                    }
                } else {
                    showErrorDialog(this, it.message ?: getString(R.string.error_message))
                }
            }
        }

        medicationReminderViewModel.doneMedicationResult.observe(this) { response ->
            response?.let {
                if (it.status == "success") {
                    showSuccessDialog(this, getString(R.string.medication_done)) {
                        medicationReminderViewModel.getListMedications(this)
                    }
                } else {
                    showErrorDialog(this, it.message ?: getString(R.string.error_message))
                }
            }
        }

    }

    private fun parseDate(dateString: String?): Date? {
        return try {
            val inputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            inputFormat.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseTime(timeString: String?): Date? {
        return try {
            val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            inputFormat.parse(timeString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun setMedicationResults(listMedicationResult: List<MedicationData>) {
        val adapter = MedicationAdapter()
        adapter.submitList(listMedicationResult)
        binding.rvMedicationList.adapter = adapter
        setupActionList(adapter)
    }

    private fun setupActionList(adapter: MedicationAdapter) {
        adapter.setOnItemClickListener(object : MedicationAdapter.OnItemClickListener {
            override fun onItemClick(medication: MedicationData) {
            }
            override fun onDeleteClick(medication: MedicationData) {
                showConfirmationDialog(
                    this@MedicationReminderActivity,
                    getString(R.string.confirm_delete_medication)
                ) {
                    medicationReminderViewModel.deleteMedication(medication.medicationName, this@MedicationReminderActivity)
                }
            }

            override fun onDoneClick(medication: MedicationData) {
                showConfirmationDialog(
                    this@MedicationReminderActivity,
                    getString(R.string.confirm_mark_done)
                ) {
                    medicationReminderViewModel.updateMedication(medication.medicationName, medication.schedule, "Done",this@MedicationReminderActivity)
                }
            }

            override fun onSkipClick(medication: MedicationData) {
                showConfirmationDialog(
                    this@MedicationReminderActivity,
                    getString(R.string.confirm_mark_skip)
                ) {
                    medicationReminderViewModel.updateMedication(medication.medicationName, medication.schedule, "Skip", this@MedicationReminderActivity)
                }
            }
        })
    }

    private fun setupCalendarRecyclerView() {
        currentCalendar = Calendar.getInstance()
        binding.calendarView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        calendarAdapter = CalendarAdapter(this, getCalendarData())
        binding.calendarView.adapter = calendarAdapter
    }

    private fun setupMonthYearPicker() {
        binding.monthYearPicker.setOnClickListener { showMonthYearPicker() }
        updateMonthYearPickerText()
    }

    private fun getCalendarData(): ArrayList<CalendarData> {
        val calendarList = ArrayList<CalendarData>()
        val calendar = Calendar.getInstance()
        calendar.time = currentCalendar.time // Set calendar to current month and year
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Start at the beginning of the month

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

        while (calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
            val date = calendar.time
            if (date >= today) {
                calendarList.add(
                    CalendarData(
                        date = date,
                        calendarDate = dateFormat.format(date),
                        calendarDay = dayFormat.format(date)
                    )
                )
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendarList
    }

    private fun showMonthYearPicker() {
        val currentYear = currentCalendar.get(Calendar.YEAR)
        val currentMonth = currentCalendar.get(Calendar.MONTH)

        // Set the minimum date to the current month and year
        val minDateCalendar = Calendar.getInstance()
        minDateCalendar.set(Calendar.YEAR, currentYear)
        minDateCalendar.set(Calendar.MONTH, currentMonth)
        val minDate = minDateCalendar.timeInMillis

        val maxDateCalendar = Calendar.getInstance()
        maxDateCalendar.add(Calendar.YEAR, 2)
        val maxDate = maxDateCalendar.timeInMillis

        val dialogFragment = MonthYearPickerDialogFragment
            .getInstance(currentMonth, currentYear, minDate, maxDate)

        dialogFragment.setOnDateSetListener { year, month ->
            currentCalendar.set(Calendar.YEAR, year)
            currentCalendar.set(Calendar.MONTH, month)
            updateMonthYearPickerText()
            updateCalendarData()
        }

        dialogFragment.show(supportFragmentManager, null)
    }

    private fun updateCalendarData() {
        calendarAdapter.updateList(getCalendarData())
    }


    private fun updateMonthYearPickerText() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        binding.monthYearPicker.text = dateFormat.format(currentCalendar.time)
    }

    override fun onItemClick(calendarData: CalendarData, position: Int) {
        selectedDate = calendarData.date
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showNoMedicationMessage(state: Boolean) {
        binding.tvNoMedication.visibility = if (state) View.VISIBLE else View.GONE

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