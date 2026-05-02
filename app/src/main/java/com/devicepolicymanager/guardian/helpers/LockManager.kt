/*© 2025 LS Creative Technologies. All rights reserved.
LS Creative Technologies is your strategic partner in innovation,
offering technology solutions that enhance performance,
streamline operations, and support long-term success.*/



package com.DevicePolicyManager.guardian.helpers

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context


class LockManager(context: Context) {
    private val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val adminComponent: ComponentName = ComponentName(context, DeviceAdminReceiver::class.java)

    fun applyHardLock() {
        if (dpm.isAdminActive(adminComponent)) {
            dpm.setPasswordMinimumLength(adminComponent, 8)
            dpm.setPasswordQuality(adminComponent, DevicePolicyManager.PASSWORD_QUALITY_NUMERIC)
            dpm.resetPassword("12345678", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY)
            dpm.lockNow()
        }
    }
}
