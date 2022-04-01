package me.invin.bookstore.utils

import android.app.Activity
import android.app.AlertDialog
import me.invin.bookstore.R


object DialogUtils {
    fun showAlertPopup(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(activity.getString(R.string.notice))
        builder.setMessage(activity.getString(R.string.dialog_body_msg))
        builder.setCancelable(false)
        builder.setPositiveButton(activity.getString(R.string.confirm)) { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }
}