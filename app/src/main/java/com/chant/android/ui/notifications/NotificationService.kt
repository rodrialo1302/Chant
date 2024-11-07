package com.chant.android.ui.notifications

import android.Manifest
import android.app.IntentService
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.chant.android.R
import com.chant.android.model.dao.NotificationDAO
import com.chant.android.model.entities.Notification
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class NotificationService : IntentService(TAG) {
    private var runFlag = false
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreated")
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onStarted")
        runFlag = true
        while (runFlag) {
            Log.d(TAG, "Updater running")
            try {

                val sharedpreferences = getSharedPreferences("shared_prefs", AppCompatActivity.MODE_PRIVATE)

                val username = sharedpreferences?.getString("user_key", null) ?: break

                val notiSize = sharedpreferences?.getInt("notifications_size", -1)

                val notifications = NotificationDAO.getNotifications(username)


                if (notifications.size > notiSize!! && notiSize != -1) {
                    val pushSize = notifications.size - notiSize!!

                    for (i in 0..pushSize -1) {

                        /*val bintent = Intent("com.chant.android.action.NEW_NOTIFICATIONS")
                        intent?.putExtra("NOTIFICATION", notifications[i] as Serializable)
                        sendBroadcast(bintent)
                        Log.d(TAG, "broadcast started")*/

                        Log.d("NotificationReceiver", "Notificaciones mandando")
                        val builder : NotificationCompat.Builder
                        if (notifications[i].type == "like") {
                            builder = NotificationCompat.Builder(this.applicationContext, "INTERACTIONS")
                                .setSmallIcon(R.drawable.ic_heart)
                                .setContentTitle("A " + notifications[i].author + " le ha gustado tu Post")
                                .setContentText(notifications[i].text)
                                .setStyle(
                                    NotificationCompat.BigTextStyle()
                                        .bigText(notifications[i].text)
                                )
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        }else{ builder = NotificationCompat.Builder(this.applicationContext, "INTERACTIONS")
                            .setSmallIcon(R.drawable.ic_comment)
                            .setContentTitle(notifications[i].author + " ha respondido a tu Post")
                            .setContentText(notifications[i].text)
                            .setStyle(
                                NotificationCompat.BigTextStyle()
                                .bigText(notifications[i].text))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        }

                        with(NotificationManagerCompat.from(this.applicationContext)) {

                            // notificationId is a unique int for each notification that you must define.
                            if (ActivityCompat.checkSelfPermission(
                                    this@NotificationService.applicationContext,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return
                            }
                            notify(createID(), builder.build())
                        }









                    }

                }

                val editor = sharedpreferences?.edit()

                editor?.putInt("notifications_size", notifications.size)

                editor?.apply()


                // FIN DATOS TWITTER
                Log.d(TAG, "Updater ran")
                Thread.sleep(DELAY.toLong())
            } catch (e: InterruptedException) {
                runFlag = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        runFlag = false
        Log.d(TAG, "onDestroyed")
    }

    private fun createID(): Int {
        val now = Date()
        return SimpleDateFormat("ddHHmmss", Locale.US).format(now).toInt()
    }


    companion object {
        const val DELAY = 30000 // medio minuto
        const val TAG = "NotificationService"
    }
}
