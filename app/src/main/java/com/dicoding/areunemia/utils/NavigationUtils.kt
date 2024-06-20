package com.dicoding.areunemia.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity

fun navigateToOtherFeature(context: Context, activityClass: Class<*>) {
    context.startActivity(Intent(context, activityClass))
    if (context is AppCompatActivity) {
        if (Build.VERSION.SDK_INT >= 34) {
            context.overrideActivityTransition(AppCompatActivity.OVERRIDE_TRANSITION_OPEN, 0, 0)
        } else {
            context.overridePendingTransition(0, 0)
        }
        context.finish()
    }
}
