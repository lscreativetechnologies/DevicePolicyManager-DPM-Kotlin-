/*© 2025 LS Creative Technologies. All rights reserved.
LS Creative Technologies is your strategic partner in innovation,
offering technology solutions that enhance performance,
streamline operations, and support long-term success.*/



package com.DevicePolicyManager.guardian.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent


class MyAccessibilityService : AccessibilityService() {
    private var handler: Handler? = null
    // A mutable list of package names you want to monitor
    private val monitoredPackages = mutableSetOf(
        "com.android.settings",
        "com.whatsapp",
        "com.facebook.katana",
        "com.instagram.android",
        "com.android.chrome",
        "org.altruist.BajajExperia"
    )

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
        Log.d("TAG", "Accessibility Service Unbound")
        // Auto-restart logic: you can attempt to restart the service here if it gets unbound
        restartService()
    }
//    override fun onUnbind(intent: Intent?): Boolean {
//        super.onUnbind(intent)
//
//    }

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo()
        info.packageNames = monitoredPackages.toTypedArray()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_VIEW_CLICKED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.notificationTimeout = 100
//        info.canRetrieveWindowContent = true

        // Set the AccessibilityService info
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName.toString()

        // Check if the package name is in the monitored list
        if (monitoredPackages.contains(packageName)) {
            // Perform your monitoring actions here for the package
        }
    }

    override fun onInterrupt() {
        // Handle interruption, if necessary
    }

    // Function to update the list of monitored packages
    fun updateMonitoredPackages(newPackages: Set<String>) {
        monitoredPackages.clear()
        monitoredPackages.addAll(newPackages)

        // Update the AccessibilityService's package names
        val info = AccessibilityServiceInfo()
        info.packageNames = monitoredPackages.toTypedArray()
        serviceInfo = info
    }

    override fun onCreate() {
        super.onCreate()
        handler =  Handler(Looper.getMainLooper()); // To handle UI tasks (if needed)
    }


    private fun restartService() {
        handler?.postDelayed(Runnable {
            // Delay a little to ensure everything is ready before restarting
            Log.d("TAG", "Attempting to restart the Accessibility Service...")
            val intent = Intent(
                applicationContext,
                MyAccessibilityService::class.java
            )
            applicationContext.startService(intent) // Restart service
            // Optionally, you could use startForegroundService() if it's a service running in the foreground.
        }, 5000) // Wait 5 seconds before trying to restart the service
    }
}
