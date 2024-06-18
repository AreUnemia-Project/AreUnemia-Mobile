package com.dicoding.areunemia.view.main

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.local.pref.CalendarData
import com.dicoding.areunemia.data.local.pref.News
import com.dicoding.areunemia.databinding.ActivityMainBinding
import com.dicoding.areunemia.utils.navigateToOtherFeature
import com.dicoding.areunemia.utils.showLoginAlertDialog
import com.dicoding.areunemia.view.ListNewsAdapter
import com.dicoding.areunemia.view.ViewModelFactory
import com.dicoding.areunemia.view.account.AccountActivity
import com.dicoding.areunemia.view.adapter.CalendarAdapter
import com.dicoding.areunemia.view.history.HistoryActivity
import com.dicoding.areunemia.view.scan.ScanActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity(), CalendarAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var newsAdapter: ListNewsAdapter
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var currentCalendar: Calendar

    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize components
        initializeComponents()

        // Observe session changes
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                // User is not logged in
                showLoginAlertDialog(this)
            } else {
                // User is logged in
                setupView()
            }
        }
    }

    private fun initializeComponents() {
        // Request necessary permissions
        if (checkAndRequestPermissions()) {
            // Permissions are granted. Initialize your components.
            setupNewsRecyclerView()
            setupCalendarRecyclerView()
            setupMonthYearPicker()
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissionsNeeded = ArrayList<String>()
        val permissionsList = arrayOf(
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CALENDAR
        )

        for (permission in permissionsList) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), PERMISSION_REQUEST_CODE)
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Permissions granted
                    setupNewsRecyclerView()
                    setupCalendarRecyclerView()
                    setupMonthYearPicker()
                } else {
                    // Permissions denied, show a message to the user
                    AlertDialog.Builder(this)
                        .setMessage("Permissions are required for the app to function correctly.")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
            }
        }
    }

    private fun setupNewsRecyclerView() {
        binding.rvBerita.layoutManager = LinearLayoutManager(this)
        newsAdapter = ListNewsAdapter(this, getLocalNewsData())
        binding.rvBerita.adapter = newsAdapter
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

    private fun getLocalNewsData(): List<News> {
        val titles = resources.getStringArray(R.array.news_titles)
        val descriptions = resources.getStringArray(R.array.news_descriptions)
        val images = resources.obtainTypedArray(R.array.news_images)

        val newsList = mutableListOf<News>()

        for (i in titles.indices) {
            val title = titles[i]
            val description = descriptions[i]
            val imageResId = images.getResourceId(i, -1)
            newsList.add(News(title, description, imageResId))
        }

        images.recycle() // Recycle the TypedArray to free up resources
        return newsList
    }

    private fun getCalendarData(): ArrayList<CalendarData> {
        val calendarList = ArrayList<CalendarData>()
        val calendar = Calendar.getInstance()
        calendar.time = currentCalendar.time // Set calendar to current month and year
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Start at the beginning of the month

        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

        while (calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
            calendarList.add(
                CalendarData(
                    date = calendar.time,
                    calendarDate = dateFormat.format(calendar.time),
                    calendarDay = dayFormat.format(calendar.time)
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendarList
    }

    private fun showMonthYearPicker() {
        val year = currentCalendar.get(Calendar.YEAR)
        val month = currentCalendar.get(Calendar.MONTH)
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, _ ->
            currentCalendar.set(Calendar.YEAR, selectedYear)
            currentCalendar.set(Calendar.MONTH, selectedMonth)
            updateMonthYearPickerText()
            updateCalendarData()
        }, year, month, currentCalendar.get(Calendar.DAY_OF_MONTH))

        // Hiding day field in DatePickerDialog
        try {
            val dayPickerId = resources.getIdentifier("android:id/day", null, null)
            if (dayPickerId != 0) {
                val dayPicker = datePickerDialog.datePicker.findViewById<View>(dayPickerId)
                dayPicker?.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        datePickerDialog.show()
    }

    private fun updateMonthYearPickerText() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        binding.monthYearPicker.text = dateFormat.format(currentCalendar.time)
    }

    private fun updateCalendarData() {
        calendarAdapter.updateList(getCalendarData())
    }

    override fun onItemClick(calendarData: CalendarData, position: Int) {
        // Show dialog to set reminder
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_medication_reminder, null)
        val editTextMedicationName = dialogView.findViewById<EditText>(R.id.editTextMedicationName)
        val editTextReminderTime = dialogView.findViewById<EditText>(R.id.editTextReminderTime)

        editTextReminderTime.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    editTextReminderTime.setText(timeFormat.format(calendar.time))
                },
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Set Medication Reminder")
            .setPositiveButton("Set") { dialog, _ ->
                val medicationName = editTextMedicationName.text.toString()
                val reminderTimeString = editTextReminderTime.text.toString()
                val reminderTime = Calendar.getInstance().apply {
                    time = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(reminderTimeString)!!
                    set(Calendar.YEAR, calendarData.date.year + 1900)  // Deprecated, consider using Calendar.YEAR
                    set(Calendar.MONTH, calendarData.date.month)  // Deprecated, consider using Calendar.MONTH
                    set(Calendar.DAY_OF_MONTH, calendarData.date.date)  // Deprecated, consider using Calendar.DAY_OF_MONTH
                }
                addEventToCalendar(reminderTime, medicationName)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addEventToCalendar(reminderTime: Calendar, medicationName: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CALENDAR), PERMISSION_REQUEST_CODE)
            return
        }

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, reminderTime.timeInMillis)
            put(CalendarContract.Events.DTEND, reminderTime.timeInMillis + 60 * 60 * 1000) // 1 hour event
            put(CalendarContract.Events.TITLE, medicationName)
            put(CalendarContract.Events.DESCRIPTION, "Medication Reminder")
            put(CalendarContract.Events.CALENDAR_ID, 1) // Assuming the user has at least one calendar, and it has an ID of 1
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

        val eventID = uri?.lastPathSegment?.toLong()
        if (eventID != null) {
            val reminderValues = ContentValues().apply {
                put(CalendarContract.Reminders.MINUTES, 10) // Reminder 10 minutes before
                put(CalendarContract.Reminders.EVENT_ID, eventID)
                put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
            }
            contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
        }
    }

    private fun setupView() {
        // Additional setup when user is logged in
        supportActionBar?.hide()
        binding.bottomNavigation.selectedItemId = R.id.bottom_home
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> true
                R.id.bottom_scan -> {
                    navigateToOtherFeature(this, ScanActivity::class.java)
                    true
                }
                R.id.bottom_history -> {
                    navigateToOtherFeature(this, HistoryActivity::class.java)
                    true
                }
                R.id.bottom_account -> {
                    navigateToOtherFeature(this, AccountActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }
}
