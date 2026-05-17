/*© 2025 HexTags Technologies.
 All rights reserved. HexTags Technologies is your strategic partner in innovation,
 offering technology solutions that enhance performance,
 streamline operations, and support long-term success*/



package com.DevicePolicyManager.guardian.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.DevicePolicyManager.guardian.R
import com.DevicePolicyManager.guardian.databinding.ActivityAccessibilityEnableBinding
import com.DevicePolicyManager.guardian.databinding.ActivityMainBinding
import com.DevicePolicyManager.guardian.helpers.AccessibilityHelper.isAccessibilityServiceEnabled
import com.DevicePolicyManager.guardian.services.FocusModeService

class AccessibilityEnableActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccessibilityEnableBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccessibilityEnableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEnableAccessibility.setOnClickListener {
            openAccessibilitySettings()
        }

    }


    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        val isServiceEnabled = isAccessibilityServiceEnabled(this, FocusModeService::class.java)

        if (isServiceEnabled) {
            finish()
        }
    }


}