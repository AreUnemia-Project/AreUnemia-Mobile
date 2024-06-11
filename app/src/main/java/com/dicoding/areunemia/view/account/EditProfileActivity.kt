package com.dicoding.areunemia.view.account

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.areunemia.R

class EditProfileActivity : AppCompatActivity() {

    private lateinit var genderAutoCompleteTextView: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        genderAutoCompleteTextView = findViewById(R.id.inputeditgen)
        val adapter = ArrayAdapter.createFromResource(this,
            R.array.gender_options, android.R.layout.simple_dropdown_item_1line)
        genderAutoCompleteTextView.setAdapter(adapter)

        // Make the dropdown appear when the field is clicked
        genderAutoCompleteTextView.setOnClickListener {
            genderAutoCompleteTextView.showDropDown()
        }
    }

    // Other methods
}
