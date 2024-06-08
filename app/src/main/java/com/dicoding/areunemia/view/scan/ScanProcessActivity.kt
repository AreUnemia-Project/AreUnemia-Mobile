package com.dicoding.areunemia.view.scan

import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.ActivityScanProcessBinding
import com.dicoding.areunemia.utils.showConfirmationDialog
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
        val targetState = when (nextFragment) {
            is EyeUploadFragment -> StateProgressBar.StateNumber.ONE
            is QuestionnaireFragment -> StateProgressBar.StateNumber.TWO
            is ConfirmScanFragment -> StateProgressBar.StateNumber.THREE
            else -> StateProgressBar.StateNumber.ONE
        }
        animateStateProgressBar(stateProgressBar, targetState)
    }

    private fun animateStateProgressBar(stateProgressBar: StateProgressBar, targetState: StateProgressBar.StateNumber) {
        val currentState = stateProgressBar.currentStateNumber
        val animator = ValueAnimator.ofInt(currentState, targetState.value)
        animator.duration = 300 // duration of the animation in milliseconds
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.entries[animatedValue - 1])
        }
        animator.start()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                showConfirmationDialog(this, getString(R.string.warning_message)) {
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
