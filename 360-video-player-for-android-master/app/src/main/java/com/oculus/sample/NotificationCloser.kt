package com.oculus.sample

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

class NotificationCloser : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("CLOSED", "closing")
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(intent.getIntExtra(NOTIFICATION_ID, -1))
        try {
            MainActivity.pauseVideo()
        } catch (e: Exception) {
            // Activity is not running
        }
        finish() // since finish() is called in onCreate(), onDestroy() will be called immediately
    }

    companion object {
        const val NOTIFICATION_ID = "NOTIFICATION_ID"

        fun getDismissIntent(notificationId: Int, context: Context): PendingIntent {
            val intent = Intent(context, NotificationCloser::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(NOTIFICATION_ID, notificationId)
            return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
