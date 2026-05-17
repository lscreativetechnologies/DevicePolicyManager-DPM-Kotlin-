/*© 2025 HexTags Technologies.
 All rights reserved. HexTags Technologies is your strategic partner in innovation,
 offering technology solutions that enhance performance,
 streamline operations, and support long-term success*/


package com.DevicePolicyManager.guardian.services


import android.R
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.DevicePolicyManager.guardian.helpers.AccessibilityHelper.isAccessibilityServiceEnabled


class AccessibilityMonitorService : Service() {
    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, notification)

        Thread {
            while (true) {
                if (!isAccessibilityServiceEnabled(
                        this,
                        FocusModeService::class.java
                    )
                ) {
                    showNotification()
                }
                try {
                    Thread.sleep(5000) // Check every 5 seconds
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Accessibility Monitor",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private val notification: Notification
        get() = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Accessibility Service Running")
            .setSmallIcon(R.drawable.ic_secure)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

    private fun showNotification() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Enable Accessibility Service")
            .setContentText("Click to enable service")
            .setSmallIcon(R.drawable.ic_secure)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(2, notification)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        private const val CHANNEL_ID = "AccessibilityMonitorChannel"
    }
}
