/*© 2025 LS Creative Technologies. All rights reserved.
LS Creative Technologies is your strategic partner in innovation,
offering technology solutions that enhance performance,
streamline operations, and support long-term success.*/



package com.DevicePolicyManager.guardian.helpers

import android.app.Activity
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build


class Policy(private val mContext: Context) {
    /**
     * Getter for password quality.
     *
     * @return
     */
    var passwordQuality: Int
        private set

    /**
     * Getter for password length.
     *
     * @return
     */
    var passwordLength: Int = 0
        private set

    /**
     * Getter for password minimum upper case alphabets.
     *
     * @return
     */
    var passwordMinUpperCase: Int = 0
        private set
    private val mDPM: DevicePolicyManager

    /**
     * Getter for the policy administrator ComponentName object.
     *
     * @return
     */
    val policyAdmin: ComponentName

    init {
        passwordQuality = -1
        mDPM = mContext.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        policyAdmin = ComponentName(mContext, PolicyAdmin::class.java)
    }

    /**
     * Saves the policy parameters.
     *
     * @param passwordQuality Password quality.
     * @param passwordLength Password minimum length.
     * @param passwordUppercase Password minimum number of upper case alphabets.
     */
    fun saveToLocal(passwordQuality: Int, passwordLength: Int, passwordMinUppercase: Int) {
        val editor =
            mContext.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit()
        if (this.passwordQuality != passwordQuality) {
            editor.putInt(KEY_PASSWORD_QUALITY, passwordQuality)
            this.passwordQuality = passwordQuality
        }
        if (this.passwordLength != passwordLength) {
            editor.putInt(KEY_PASSWORD_LENGTH, passwordLength)
            this.passwordLength = passwordLength
        }
        if (passwordMinUpperCase != passwordMinUppercase) {
            editor.putInt(KEY_PASSWORD_MIN_UPPERCASE, passwordMinUppercase)
            passwordMinUpperCase = passwordMinUppercase
        }
        editor.commit()
    }

    fun readFromLocal() {
        val prefs = mContext.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE)
        passwordQuality = prefs.getInt(KEY_PASSWORD_QUALITY, -1)
        passwordLength = prefs.getInt(KEY_PASSWORD_LENGTH, -1)
        passwordMinUpperCase = prefs.getInt(KEY_PASSWORD_MIN_UPPERCASE, -1)
    }

    val isAdminActive: Boolean
        /**
         * Indicates whether the device administrator is currently active.
         *
         * @return
         */
        get() = mDPM.isAdminActive(policyAdmin)
    val isActivePasswordSufficient: Boolean
        get() = mDPM.isActivePasswordSufficient
    val isDeviceSecured: Boolean
        get() = isAdminActive && isActivePasswordSufficient

    /**
     * Configure policy defined in the object.
     */
    fun configurePolicy() {
        mDPM.setPasswordQuality(
            policyAdmin,
            PASSWORD_QUALITY_VALUES[passwordQuality]
        )
        mDPM.setPasswordMinimumLength(policyAdmin, passwordLength)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mDPM.setPasswordMinimumUpperCase(policyAdmin, passwordMinUpperCase)
        }
    }

    /**
     * Through the PolicyAdmin receiver, the app can use this to trap various device
     * administration events, such as password change, incorrect password entry, etc.
     *
     */
    class PolicyAdmin : DeviceAdminReceiver() {
        override fun onDisabled(context: Context, intent: Intent) {
            // Called when the app is about to be deactivated as a device administrator.
            // Deletes previously stored password policy.
            super.onDisabled(context, intent)
            val prefs = context.getSharedPreferences(APP_PREF, Activity.MODE_PRIVATE)
            prefs.edit().clear().commit()
        }
    }

    companion object {
        const val REQUEST_ADD_DEVICE_ADMIN: Int = 1
        private const val APP_PREF = "APP_PREF"
        private const val KEY_PASSWORD_LENGTH = "PW_LENGTH"
        private const val KEY_PASSWORD_QUALITY = "PW_QUALITY"
        private const val KEY_PASSWORD_MIN_UPPERCASE = "PW_MIN_UPPERCASE"

        // Password quality values.  This list must match the list
        // found in res/values/arrays.xml
        val PASSWORD_QUALITY_VALUES: IntArray = intArrayOf(
            DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED,
            DevicePolicyManager.PASSWORD_QUALITY_SOMETHING,
            DevicePolicyManager.PASSWORD_QUALITY_NUMERIC,
            DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC,
            DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC,
            DevicePolicyManager.PASSWORD_QUALITY_COMPLEX
        )
    }
}