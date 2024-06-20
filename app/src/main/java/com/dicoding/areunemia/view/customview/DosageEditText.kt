package com.dicoding.areunemia.view.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.areunemia.R

class DosageEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null) : AppCompatEditText(context, attrs) {

    init {
        inputType = InputType.TYPE_CLASS_NUMBER

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Check if the input is a valid integer
                if (s.isNullOrEmpty() || !s.toString().isDigitsOnly()) {
                    setError(context.getString(R.string.dosage_error_message), null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun CharSequence.isDigitsOnly(): Boolean {
        for (element in this) {
            if (!Character.isDigit(element)) {
                return false
            }
        }
        return true
    }
}