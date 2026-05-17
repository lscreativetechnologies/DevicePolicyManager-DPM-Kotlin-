/*© 2025 HexTags Technologies.
 All rights reserved. HexTags Technologies is your strategic partner in innovation,
 offering technology solutions that enhance performance,
 streamline operations, and support long-term success*/



package com.DevicePolicyManager.guardian.recievers

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log

open class MyDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)

        // Show a toast to confirm that the DPC was enabled
//        Toast.makeText(context, "Device Admin enabled!", Toast.LENGTH_SHORT).show()

        //disable keyguard
        /*val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val attrs = WindowManager.LayoutParams(1, 1)
        attrs.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        val dummyView = View(context)
        windowManager.addView(dummyView, attrs)
        windowManager.removeView(dummyView)*/

//        // Enforce a password policy (e.g., require a minimum length for passwords)
//        enforcePasswordPolicy(context)
//
//        // Hide a specific application (to prevent uninstallation)
//        hideApplication(context, "com.DevicePolicyManager.guardian")
//        (this as MainActivity).setPackagesSuspended()

        // You can add more logic here as needed
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)

        // Lock the device as soon as admin is being disabled
        // This will lock the device when disabling the admin
        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        devicePolicyManager.lockNow()
        // Show a toast to confirm that the DPC was disabled
//        Toast.makeText(context, "Device Admin disabled!", Toast.LENGTH_SHORT).show()
    }


    // Method to enforce a password policy
    private fun enforcePasswordPolicy(context: Context) {
        val devicePolicyManager =
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponent = ComponentName(context, MyDeviceAdminReceiver::class.java)

        // Set a minimum password length of 8 characters
        devicePolicyManager.setPasswordQuality(
            adminComponent,
            DevicePolicyManager.PASSWORD_QUALITY_COMPLEX
        )
        devicePolicyManager.setPasswordMinimumLength(adminComponent, 8)

        // You can also set other password policies like expiration, history, etc.
    }

    // Method to hide an application (this does not prevent uninstallation but makes the app hidden)
    private fun hideApplication(context: Context, packageName: String) {
        val devicePolicyManager =
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponent = ComponentName(context, MyDeviceAdminReceiver::class.java)

        // Hide the app to make it more difficult for users to uninstall
        devicePolicyManager.setApplicationHidden(adminComponent, packageName, true)
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("TAG", "ShutdownReceiver received an intent!")

        when (intent.action) {
            Intent.ACTION_SHUTDOWN -> {
                // Handle shutdown action
                Log.d("TAG", "System is shutting down.")
                // Save data or other shutdown logic
            }
            "android.intent.action.QICKBOOT_POWEROFF" -> {
                // Handle Quickboot power off
                Log.d("TAG", "Quickboot Power Off")
            }
            else -> {
                Log.d("TAG", "Unknown intent action: ${intent.action}")
            }
        }
    }

}


