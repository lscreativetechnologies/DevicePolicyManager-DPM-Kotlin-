/*© 2025 HexTags Technologies.
 All rights reserved. HexTags Technologies is your strategic partner in innovation,
 offering technology solutions that enhance performance,
 streamline operations, and support long-term success*/



package com.DevicePolicyManager.guardian.ui

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.DevicePolicyManager.guardian.R

class KioskModeActivity : AppCompatActivity() {
    private var dpm: DevicePolicyManager? = null
    private var adminComponent: ComponentName? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_kiosk_mode)
        dpm = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, DeviceAdminReceiver::class.java)

        if (dpm!!.isDeviceOwnerApp(packageName)) {
            // Enable Lock Task Mode
            val allowedApps = arrayOf(
                "com.android.dialer",  // Calls
                "com.android.mms",  // SMS
                "com.android.chrome",  // Essential browsing (optional)
                "com.android.settings" // Settings (minimal)
            )
            dpm!!.setLockTaskPackages(adminComponent, allowedApps)
            startLockTask()

        }
    }
}