package com.chant.android.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.chant.android.ui.notifications.NotificationService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            Thread.sleep(3000)
            context.startService(Intent(context, NotificationService::class.java))
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        Log.d("BootReceiver", "onReceived")
    }
}