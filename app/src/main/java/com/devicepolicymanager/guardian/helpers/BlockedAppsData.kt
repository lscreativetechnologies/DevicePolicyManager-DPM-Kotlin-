/*© 2025 LS Creative Technologies. All rights reserved.
LS Creative Technologies is your strategic partner in innovation,
offering technology solutions that enhance performance,
streamline operations, and support long-term success.*/



package com.DevicePolicyManager.guardian.helpers

data class BlockedAppsData(
    val id: Int = (1000..9999).random(),
    val packageName: String,
    val isEnabled: Boolean
)
