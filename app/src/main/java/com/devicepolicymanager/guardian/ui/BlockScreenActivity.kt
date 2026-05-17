/*© 2025 HexTags Technologies.
 All rights reserved. HexTags Technologies is your strategic partner in innovation,
 offering technology solutions that enhance performance,
 streamline operations, and support long-term success*/



package com.DevicePolicyManager.guardian.ui

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView


class BlockScreenActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prevent user from bypassing by pressing home or back
        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val textView = TextView(this)
        textView.setText("Access to apps is blocked.")
        textView.setTextSize(24f)
        textView.setPadding(50, 200, 50, 200)
        setContentView(textView)
    }

    override fun onBackPressed() {
        // Block back button
    }
}