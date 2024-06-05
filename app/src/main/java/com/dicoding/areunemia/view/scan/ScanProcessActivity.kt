package com.dicoding.areunemia.view.scan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.ActivityScanProcessBinding
import com.kofigyan.stateprogressbar.StateProgressBar

class ScanProcessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanProcessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        setupStateProgressBar()
        loadFragment(EyeUploadFragment()) // Load the first fragment
    }

    private fun setupStateProgressBar() {
        val stateProgressBar = binding.stateProgressBar
        val stepsArray = resources.getStringArray(R.array.scan_process_steps)
        stateProgressBar.setStateDescriptionData(stepsArray)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun navigateToNextFragment(currentFragment: Fragment) {
        val nextFragment = when (currentFragment) {
            is EyeUploadFragment -> QuestionnaireFragment()
            is QuestionnaireFragment -> ConfirmScanFragment()
            else -> null
        }

        if (nextFragment != null) {
            slideToNextFragment(nextFragment)
            updateStateProgressBar(nextFragment)
        } else {
            // Handle case when there is no next fragment
        }
    }

    private fun slideToNextFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun updateStateProgressBar(nextFragment: Fragment) {
        val stateProgressBar = binding.stateProgressBar
        when (nextFragment) {
            is EyeUploadFragment -> stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE)
            is QuestionnaireFragment -> stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
            is ConfirmScanFragment -> stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE)
            else -> stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                showConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.warning))
            .setMessage(getString(R.string.warning_message))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }
}
