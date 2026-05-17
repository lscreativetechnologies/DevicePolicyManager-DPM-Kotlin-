/*© 2025 HexTags Technologies.
 All rights reserved. HexTags Technologies is your strategic partner in innovation,
 offering technology solutions that enhance performance,
 streamline operations, and support long-term success*/



package com.DevicePolicyManager.guardian.helpers

import android.content.ComponentName
import android.content.Context
import android.provider.Settings


object AccessibilityHelper {
    fun isAccessibilityServiceEnabled(context: Context, service: Class<*>): Boolean {

        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )

        if (enabledServices != null) {
            for (enabledService in enabledServices.split(":".toRegex())
                .dropLastWhile { it.isEmpty() }.toTypedArray()) {
                val componentName = ComponentName.unflattenFromString(enabledService)
                if (componentName != null && componentName.className == service.name) {
                    return true
                }
            }
        }
        return false
    }
}
