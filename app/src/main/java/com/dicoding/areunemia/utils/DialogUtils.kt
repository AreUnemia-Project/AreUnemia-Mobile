package com.dicoding.areunemia.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dicoding.areunemia.R
import com.dicoding.areunemia.view.login.LoginActivity
import com.dicoding.areunemia.view.main.MainActivity

fun showErrorDialog(context: Context, message: String?) {
    AlertDialog.Builder(context).apply {
        setTitle(context.getString(R.string.error_title))
        setMessage(message)
        setPositiveButton(context.getString(R.string.ok), null)
        create()
        show()
    }
}

fun showSuccessDialog(context: Context, message: String, onContinue: () -> Unit) {
    AlertDialog.Builder(context).apply {
        setTitle(context.getString(R.string.success))
        setMessage(message)
        setPositiveButton(context.getString(R.string.continue_text)) { _, _ ->
            onContinue()
        }
        create()
        show()
    }
}

fun showWarningDialog(context: Context, message: String, onContinue: () -> Unit) {
    AlertDialog.Builder(context).apply {
        setTitle(context.getString(R.string.warning))
        setMessage(message)
        setPositiveButton(context.getString(R.string.continue_text)) { _, _ ->
            onContinue()
        }
        setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        create()
        show()
    }
}

fun showLoginAlertDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle(R.string.login_required)
        .setMessage(R.string.need_to_login)
        .setPositiveButton(R.string.yes) { _, _ ->
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
        .setNegativeButton(R.string.no) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

fun showConfirmationDialog(context: Context, message: String, onContinue: () -> Unit) {
    AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.confirm))
        .setMessage(message)
        .setPositiveButton(context.getString(R.string.yes)) { dialog, _ ->
            dialog.dismiss()
            onContinue()
        }
        .setNegativeButton(context.getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()
}

fun showLogoutAlertDialog(context: Context) {
    AlertDialog.Builder(context).apply {
        setTitle(context.getString(R.string.session_expired))
        setMessage(context.getString(R.string.session_expired_message))
        setPositiveButton(context.getString(R.string.login)) { _, _ ->
            context.startActivity(Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            context.startActivity(Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
        setNegativeButton(context.getString(R.string.back)) { _, _ ->
            context.startActivity(Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
        setCancelable(false) // Prevent user from dismissing the dialog
        create()
        show()
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
