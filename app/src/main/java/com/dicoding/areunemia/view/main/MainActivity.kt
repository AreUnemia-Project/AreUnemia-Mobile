package com.dicoding.areunemia.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.local.pref.News
import com.dicoding.areunemia.data.remote.response.MedicationData
import com.dicoding.areunemia.databinding.ActivityMainBinding
import com.dicoding.areunemia.utils.navigateToOtherFeature
import com.dicoding.areunemia.utils.showErrorDialog
import com.dicoding.areunemia.view.ListNewsAdapter
import com.dicoding.areunemia.view.ViewModelFactory
import com.dicoding.areunemia.view.account.AccountActivity
import com.dicoding.areunemia.view.adapter.MedicationPreviewAdapter
import com.dicoding.areunemia.view.history.HistoryActivity
import com.dicoding.areunemia.view.login.LoginActivity
import com.dicoding.areunemia.view.scan.ScanActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var newsAdapter: ListNewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.selectedItemId = R.id.bottom_home
        setupBottomNavigation()
        setupView()
        setupNewsRecyclerView()

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                toggleMedicationPreviewFeatures(false)
            } else {
                toggleMedicationPreviewFeatures(true)
                observeViewModel()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getListMedications(this)
    }

    private fun toggleMedicationPreviewFeatures(show: Boolean) {
        binding.apply {
            if (show) {
                reminderLayout1.visibility = View.GONE
                tvViewAll.visibility = View.VISIBLE
                tvViewAll.setOnClickListener {
                    val intent = Intent(this@MainActivity, MedicationReminderActivity::class.java)
                    startActivity(intent)
                }
            } else {
                reminderLayout1.visibility = View.VISIBLE
                reminderLayout2.visibility = View.GONE
                tvViewAll.visibility = View.GONE
                tvViewAll.setOnClickListener(null)
                loginButton.setOnClickListener {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                }
            }
        }
    }


    private fun setupNewsRecyclerView() {
        binding.rvBerita.layoutManager = LinearLayoutManager(this)
        newsAdapter = ListNewsAdapter(this, getLocalNewsData())
        binding.rvBerita.adapter = newsAdapter
    }

    private fun getLocalNewsData(): List<News> {
        val titles = resources.getStringArray(R.array.news_titles)
        val descriptions = resources.getStringArray(R.array.news_descriptions)
        val images = resources.obtainTypedArray(R.array.news_images)
        val dates = resources.getStringArray(R.array.news_dates)

        val newsList = mutableListOf<News>()

        for (i in titles.indices) {
            val title = titles[i]
            val description = descriptions[i]
            val imageResId = images.getResourceId(i, -1)
            val date = dates[i]
            newsList.add(News(title, description, imageResId, date))
        }

        images.recycle() // Recycle the TypedArray to free up resources
        return newsList
    }

    private fun setupView() {
        supportActionBar?.hide()
        viewModel.getListMedications(this)
        val layoutManager = LinearLayoutManager(this)
        binding.rvMedicationList.layoutManager = layoutManager
    }

    private fun observeViewModel() {
        viewModel.listMedication.observe(this) { medResult ->
            if (medResult.isNullOrEmpty()) {
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
                    .take(2) // Pick only the top two items

                if (medicationDataList.isEmpty()) {
                    showNoMedicationMessage(true)
                } else {
                    setMedicationResults(medicationDataList)
                    showNoMedicationMessage(false)
                }
            }
        }

        viewModel.error.observe(this) { message ->
            message?.let {
                if (it == "No medications found") {
                    showNoMedicationMessage(true)
                } else {
                    val errorMessage = when (it) {
                        "User ID is required" -> getString(R.string.user_id_required)
                        "Error getting medications" -> getString(R.string.failed_get_medication)
                        else -> it
                    }
                    showErrorDialog(this, errorMessage)
                }
            }
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
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
        val adapter = MedicationPreviewAdapter()
        adapter.submitList(listMedicationResult)
        binding.rvMedicationList.adapter = adapter
        setupActionList(adapter)
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showNoMedicationMessage(state: Boolean) {
        binding.reminderLayout2.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun setupActionList(adapter: MedicationPreviewAdapter) {
        adapter.setOnItemClickListener(object : MedicationPreviewAdapter.OnItemClickListener {
            override fun onItemClick(medication: MedicationData) {
                val intent = Intent(this@MainActivity, MedicationReminderActivity::class.java)
                startActivity(intent)
            }
        })
    }

    private fun setupBottomNavigation() {
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
