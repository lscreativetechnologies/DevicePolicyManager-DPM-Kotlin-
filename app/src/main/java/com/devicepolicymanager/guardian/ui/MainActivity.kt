/*© 2025 LS Creative Technologies. All rights reserved.
LS Creative Technologies is your strategic partner in innovation,
offering technology solutions that enhance performance,
streamline operations, and support long-term success.*/



package com.DevicePolicyManager.guardian.ui

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.Activity
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.DevicePolicyManager.guardian.R
import com.DevicePolicyManager.guardian.databinding.ActivityMainBinding
import com.DevicePolicyManager.guardian.helpers.AccessibilityHelper
import com.DevicePolicyManager.guardian.helpers.AccessibilityHelper.isAccessibilityServiceEnabled
import com.DevicePolicyManager.guardian.helpers.BlockedAppsData
import com.DevicePolicyManager.guardian.helpers.LockManager
import com.DevicePolicyManager.guardian.recievers.MyDeviceAdminReceiver
import com.DevicePolicyManager.guardian.services.AccessibilityMonitorService
import com.DevicePolicyManager.guardian.services.FocusModeService
import com.DevicePolicyManager.guardian.services.MyForegroundService
import com.google.android.material.snackbar.Snackbar
import java.security.SecureRandom
import java.util.Date
import kotlin.collections.mutableSetOf
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {
    private var blockedApps: MutableSet<String> = mutableSetOf()
    private var allowedApps: MutableSet<String> = mutableSetOf()
    private var blockedAppsList: MutableList<BlockedAppsData> = mutableListOf()
    private lateinit var binding: ActivityMainBinding
    private val REQUEST_CODE_ENABLE_ADMIN: Int = 1
    private var devicePolicyManager: DevicePolicyManager? = null
    private var mAdminComponent: ComponentName? = null
    private val TAG: String = "DevicePolicyController"
    private val enableAdminLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
//                Toast.makeText(this, "Device Admin enabled!", Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(this, "Device Admin not enabled!", Toast.LENGTH_SHORT).show()
            }
        }


    private var isClicked = false

    /*private lateinit var powerButtonReceiver: PowerButtonReceiver*/
    private var powerManager: PowerManager? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private val REQUEST_ENABLE_ADMIN = 1
    private var PACKAGE_TO_SUSPEND = ""


    companion object {
        private val DEFAULT_SETTINGS_PACKAGES = setOf(
            "com.android.settings",
            "com.zte.settings",
            "com.samsung.android.settings",
            "com.vivo.settings",
            "com.motorola.settings"
        )
    }

    // Method to remove a specific package from blockedApps
    private fun removeBlockedApp(packageName: String) {
        if (!blockedApps.contains(packageName)) {
            return
        }
        blockedApps.remove(packageName)
        if (DEFAULT_SETTINGS_PACKAGES.contains(packageName)) {
            blockedApps.removeAll(DEFAULT_SETTINGS_PACKAGES)
        }
//        saveBlockedApps(blockedApps)
    }


    private fun saveBlockedApps(blockedApps: MutableSet<String>) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("blockedApps", blockedApps)
        editor.apply()
    }


    fun getInstalledKeyboardPackages(context: Context): List<String> {
        val imePackages = mutableListOf<String>()
        val packageManager = context.packageManager
        val intent = Intent("android.view.InputMethod")

        val resolveInfos = packageManager.queryIntentServices(intent, PackageManager.MATCH_ALL)

        for (info in resolveInfos) {
            info.serviceInfo?.packageName?.let {
                if (!imePackages.contains(it)) {
                    imePackages.add(it)
                }
            }
        }

        return imePackages
    }


    fun getDefaultLauncherPackage(context: Context): String? {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val resolveInfo =
            context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo?.activityInfo?.packageName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val packageManager = packageManager
        val installedPackages = packageManager.getInstalledPackages(0)

        val packageNames = installedPackages.map { it.packageName }

        for (pkg in packageNames) {
            if (!getInstalledKeyboardPackages(this).contains(pkg) && pkg != "com.DevicePolicyManager.guardian" && pkg != getDefaultLauncherPackage(
                    this
                )
            ) {
                BlockedAppsData(packageName = pkg, isEnabled = true)
                blockedApps.add(pkg)
                Log.d("InstalledApp", pkg)
                Log.d("New updated values", blockedApps.toString())

            }
        }

        Log.d("InstalledKEybard", getInstalledKeyboardPackages(this).toString())

        saveBlockedApps(blockedApps)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Request Bluetooth permission at runtime for API level 31 and higher
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_SCAN), 1
                )
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val servicesIntent = Intent(this, MyForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(servicesIntent)
        } else {
            startService(servicesIntent)
        }

        binding.unblockApps.setOnClickListener {
            paymentDueDialog()
//            showPopupWindow(binding.unblockApps)
        }

        binding.btnEnableAccessibility.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                var dialog = AlertDialog.Builder(this)
                    .setTitle("Enable Accessibility Service")
                    .setMessage(
                        "To enable Accessibility for this app on Android 14 and above:\n" +
                                "\n" +
                                "1. Tap \"Open Accessibility Settings\" below.\n" +
                                "2. In the list, find and select this app.\n" +
                                "3. Toggle the Accessibility service ON.  \n" +
                                "   - If a popup appears saying \"App restricted,\" acknowledge it.\n" +
                                "4. After this, return to the App Info screen for this app.\n" +
                                "5. The \"Allow restricted settings\" option will now appear (either as a toggle or under the three-dot menu in the top right).\n" +
                                "6. Enable \"Allow restricted settings,\" then return and enable the Accessibility service again if needed.\n" +
                                "\n" +
                                "If you do not see the \"Allow restricted settings\" option, please make sure you have toggled the Accessibility service at least once."
                    )
                    .setCancelable(true)
                    .setPositiveButton("Open App info Settings") { dialog, _ ->
                        dialog.dismiss()
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = "package:${packageName}".toUri()
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    }
                    .setNegativeButton("Open Accessibility Settings") { dialog, _ ->
                        dialog.dismiss()
                        openAccessibilitySettings(this)
                    }
                    .create()

                dialog.show()
            } else {
                openAccessibilitySettings(this)
            }

        }

        binding.applyLoan.setOnClickListener {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Please pay your previous loan amount.",
                3000
            ).show()
        }

        binding.historyBUtton.setOnClickListener {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Please check recent transactions.",
                3000
            ).show()
        }

        binding.payEmi.setOnClickListener {
            openPopupForAppBlockingMessage()
        }

        binding.lockApps.setOnClickListener {
            if (!isClicked) {
                isClicked = true
                saveBlockedApps(blockedApps)
                suspendApp(PACKAGE_TO_SUSPEND)
            } else {
                isClicked = false
//                Toast.makeText(this, "All applications are already blocked.", Toast.LENGTH_SHORT)
//                    .show()
            }
        }

        getWholeInstallPackages()

        LockManager(this).applyHardLock()

        val isServiceEnabled = isAccessibilityServiceEnabled(this, FocusModeService::class.java)
        Log.e("AccessibilityIsEnabled", isServiceEnabled.toString())

        val serviceIntent = Intent(
            this,
            AccessibilityMonitorService::class.java
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        }

        mAdminComponent = ComponentName(applicationContext, MyDeviceAdminReceiver::class.java)
        binding.btnEnableAdmin.setOnClickListener {
            requestDeviceAdmin()
        }
        mDevicePolicyManagerFun(isServiceEnabled)

//        saveBlockedApps(blockedApps)
//        suspendApp(PACKAGE_TO_SUSPEND)

//        changePasswordWithToken()

        // Create the receiver
//        powerButtonReceiver = PowerButtonReceiver()
//        // Register the receiver
//        val filter = IntentFilter().apply {
//            addAction(Intent.ACTION_SCREEN_OFF)
//            addAction(Intent.ACTION_SCREEN_ON)
//        }
//        registerReceiver(powerButtonReceiver, filter)

        binding.rootBtn.setOnClickListener {
            disablePowerButton()
        }

        binding.rootBtn2.setOnClickListener {
            enablePowerButton()
        }

//        logSystemAndUserApps()

//        Log.d("socialapps", getSocialMediaPackagesByCategory(context: Context))
    }


    fun getSocialMediaPackagesByCategory(context: Context): List<String> {
        val socialMediaPackages = mutableListOf<String>()
        val packageManager = context.packageManager
        val installedPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (pkg in installedPackages) {
                val appInfo = pkg.applicationInfo
                if (appInfo?.category == ApplicationInfo.CATEGORY_SOCIAL) {
                    socialMediaPackages.add(pkg.packageName)
                }
            }
        }

        return socialMediaPackages
    }


    private fun isSystemApp(appInfo: ApplicationInfo): Boolean {
        return (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
    }

    /*private fun logSystemAndUserApps() {
        val pm = packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        val systemApps = apps.filter { isSystemApp(it) }
        val userApps = apps.filterNot { isSystemApp(it) }
        val packageSet: MutableSet<String> = mutableSetOf()
        Log.d("AppList", "System apps (${systemApps.size}):")
        systemApps.forEach {
            packageSet.add(it.packageName)

        }

        Log.d("AppList", "User apps (${userApps.size}):")
        userApps.forEach {
            Log.d("UserApp", it.packageName)
            packageSet.add(it.packageName)
        }

        saveBlockedApps(packageSet)

    }*/

    private fun openPopupForAppBlockingMessage() {
        if (!isFinishing && !isDestroyed) {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Notice")
                .setMessage("Your payment is due. Your mobile's all applications has been blocked. You have to pay to unblock the applications.")
                .setPositiveButton("Pay amount") { dialogInterface, _ ->
                    paymentDueDialog()
                }
                .setNegativeButton("OK") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
            dialog.setCancelable(false)
            dialog.show()
        }
    }


    fun paymentDueDialog() {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.setCancelable(false)

        val title = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val input = dialogView.findViewById<EditText>(R.id.inputField)
        val button = dialogView.findViewById<Button>(R.id.dialogButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)


        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        button.setOnClickListener {
            hideSoftKeyboard(input)
            val userInput = input.text.toString()
            if (userInput.isEmpty()) {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Please enter amount",
                    3000
                ).show()
            } else {
                if (userInput == "100") {
                    blockedApps.clear()
                    saveBlockedApps(blockedApps)
                    binding.activeLoanTV.text = "Loan Closed"
                    binding.textNextEMI.visibility = View.VISIBLE
                    dialog.dismiss()
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Your payment has been successfully completed. Your all applications has been unblocked.",
                        5000
                    ).show()
                } else if (userInput > "100") {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "You can't pay more than overdue amount.",
                        3000
                    ).show()
                } else if (userInput < "100") {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "You can't pay less than overdue amount.",
                        3000
                    ).show()
                }
            }
        }
        dialog.show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val isServiceEnabled = isAccessibilityServiceEnabled(this, FocusModeService::class.java)

        if (devicePolicyManager!!.isAdminActive(mAdminComponent!!)) {
            binding.deviceAdminScreen.visibility = View.GONE
            if (!isServiceEnabled) {
                binding.accessibilityScreen.visibility = View.VISIBLE
//                val intent = Intent(this@MainActivity, AccessibilityEnableActivity::class.java)
//                startActivity(intent)
//                showAccessibilityPrompt(this)
//                openAccessibilitySettings(this)
            } else {
                binding.accessibilityScreen.visibility = View.GONE
//                val serviceIntent = Intent(this, MyForegroundService::class.java)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    startForegroundService(serviceIntent)
//                } else {
//                    startService(serviceIntent)
//                }
//                saveBlockedApps(blockedApps)
                suspendApp(PACKAGE_TO_SUSPEND)
//                openPopupForAppBlockingMEssage()
            }
        } else {
            binding.deviceAdminScreen.visibility = View.VISIBLE
        }


//        val handlerOBJ = android.os.Handler()
//        handlerOBJ.postDelayed(Runnable {
//            // YOUR WORK
//            lockDevice(this)
//        }, 10000) // 10S delay
//        disablePowerButtonNew()
    }

    private fun mDevicePolicyManagerFun(isServiceEnabled: Boolean) {
        devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mAdminComponent = ComponentName(applicationContext, MyDeviceAdminReceiver::class.java)

        // Check if the admin is already enabled
        if (!devicePolicyManager!!.isAdminActive(mAdminComponent!!)) {
            binding.deviceAdminScreen.visibility = View.VISIBLE
//            showAdminRequiredDialog()

        } else {
            binding.deviceAdminScreen.visibility = View.GONE
//            Toast.makeText(this, "Device Admin is already active", Toast.LENGTH_SHORT).show()
            if (!isServiceEnabled) {
                binding.accessibilityScreen.visibility = View.VISIBLE
//                showAccessibilityPrompt(this)
//                openAccessibilitySettings(this)
//                disablePowerButton()
            } else {
                binding.accessibilityScreen.visibility = View.GONE
                openPopupForAppBlockingMessage()
//                Toast.makeText(
//                    this,
//                    "Accessibility Service is already enabled!",
//                    Toast.LENGTH_SHORT
//                ).show()
//                startActivityForResult(intent, REQUEST_ENABLE_ADMIN)
            }
//            suspendApp(PACKAGE_TO_SUSPEND)

//                try {
//                    devicePolicyManager.setLockTaskPackages(mAdminComponent, arrayOf(packageName))
//                    startLockTask()
//                } catch (e: Exception){
//                    Log.d(TAG, "startLockTask : " + e.message.toString())
//                }

        }

        //This is for app prevent app uninstall And to enable app accessibility from settings. code START
//        if (!mDevicePolicyManager!!.isAdminActive(mAdminComponent!!)) {
//            // If not, prompt the user to activate it
//            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
//            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminComponent)
//            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You need to enable device admin to manage security settings.")
////            enableAdminLauncher.launch(intent, REQUEST_CODE_ENABLE_ADMIN) // Launch the intent using the ActivityResultLauncher
//            startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN)
//            Toast.makeText(this, "Device Admin is not active!", Toast.LENGTH_SHORT).show()
//
//            /*********************************Test code here, START*****************************************************/
//
////            if (mDevicePolicyManager!!.isDeviceOwnerApp(packageName)) {
////                mDevicePolicyManager!!.setCameraDisabled(mAdminComponent, true)
////                mDevicePolicyManager!!.setLockTaskPackages(
////                    mAdminComponent,
////                    arrayOf<String>("com.android.chrome", "com.android.dialer")
////                )
////            }
//
//            /*********************************Test code here, END*****************************************************/
//        }
//        else {
//            Toast.makeText(this, "Device Admin is active!", Toast.LENGTH_SHORT).show()
//            preventUninstall(this, applicationContext.packageName)
//            if (!isEnabled) {
//                openAccessibilitySettings(this)
//            } else {
//                Toast.makeText(
//                    this,
//                    "Accessibility Service is already enabled!",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//        }

    }


    private fun showAdminRequiredDialog() {
        AlertDialog.Builder(this)
            .setTitle("Device Admin Required")
            .setMessage("To authorize a loan, you must enable device admin permissions. This is required for security compliance.")
            .setCancelable(false)
            .setPositiveButton(
                "Activate",
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    requestDeviceAdmin() // Re-launch activation
                })
            .setNegativeButton(
                "Exit App",
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    finish() // Or redirect elsewhere
                })
            .show()
    }

    private fun requestDeviceAdmin() {
        // Prompt the user to activate Device Admin
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminComponent)
        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "You need to enable device admin to manage security settings."
        )
        enableAdminLauncher.launch(intent)  // Use ActivityResultLauncher to start the intent
    }


    fun showAccessibilityPrompt(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Enable Accessibility Service")
            .setMessage("To continue, please enable accessibility permissions for this app in your device settings.")
            .setCancelable(false)
            .setPositiveButton("Open Settings") { dialog, _ ->
                dialog.dismiss()
                openAccessibilitySettings(this)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }


    private fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
       // val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        context.startActivity(intent)
    }

    fun setPackagesSuspended() {
        // Example using setPackagesSuspended()
        val dpm = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminName = ComponentName(this, DeviceAdminReceiver::class.java)
        val packageName = applicationContext.packageName // Package name of the app to restrict
        dpm.setPackagesSuspended(adminName, arrayOf(packageName), true) // Suspend the app
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
//            REQUEST_ENABLE_ADMIN -> {
//                if (resultCode == Activity.RESULT_OK) {
//                    Toast.makeText(this, "Admin enabled", Toast.LENGTH_SHORT).show()
//                    // Now you can call DevicePolicyManager methods
//                    suspendApp(PACKAGE_TO_SUSPEND) //suspend the app.
//                } else {
//                    Toast.makeText(this, "Admin enable failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    private fun suspendApp(packageName: String) {
//        saveBlockedApps(blockedApps)
        try {
            devicePolicyManager!!.setPackagesSuspended(
                mAdminComponent, arrayOf(packageName),
                true //suspend = true, false = unsuspend
            )
            if (devicePolicyManager!!.isPackageSuspended(mAdminComponent, packageName)) {
                Toast.makeText(this, "$packageName suspended", Toast.LENGTH_SHORT).show()
                Log.d("SuspendApp", "suspended:  true")


            } else {
                Log.d("SuspendApp", "suspended:  false")
                Toast.makeText(this, "$packageName not suspended", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.d("SuspendApp", "Error suspending app: ${e.message}")
//            Toast.makeText(this, "Failed to suspend app: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun preventUninstall(context: Context, packageName: String?) {
        // Get DevicePolicyManager system service
        if (devicePolicyManager!!.isAdminActive(mAdminComponent!!)) {
//            Toast.makeText(this, "Admin is active!", Toast.LENGTH_SHORT).show()
            val restrictions: Bundle = Bundle().apply {
                putBoolean("disable_install_apps", true) // Prevent app installations
                putBoolean("disable_uninstall_apps", true) // Prevent app uninstallation
                putBoolean("disable_factory_reset", true) // Prevent factory reset
                putBoolean("disable_safe_mode", true) // Prevent safe mode
                putBoolean("disable_add_user", true) // Prevent adding new users
            }

            // More keys and values can be added based on your app's restriction needs
            // Apply the restrictions to the specified package (app)
            devicePolicyManager!!.setApplicationRestrictions(
                mAdminComponent,
                packageName,
                restrictions
            )
        } else {
//            Toast.makeText(this, "Admin is NOT active!", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun applyRestrictions(context: Context?) {

        // Check if the app has device administrator privileges
        if (!hasDeviceAdminPrivileges(this)) {
            Log.e(TAG, "Device administrator privileges not granted. Cannot apply restrictions.")
            return  // Exit if not a device administrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (R) and above: Use DevicePolicyManager.setPolicy()
            disableRebootOptionR(this)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0 (Oreo) and above: Use DevicePolicyManager.setPolicy()
            disableRebootOptionOreo(this)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7.0 (Nougat) and above:  (Less reliable, may not work on all devices)
            disableRebootOptionNougat(this)
        } else {
            Log.w(
                TAG,
                "Older Android versions not fully supported for disabling reboot.  Consider upgrading."
            )
        }
    }

    private fun hasDeviceAdminPrivileges(context: Context): Boolean {
        val pm = context.packageManager
        try {
            val dpm = context.getSystemService(DevicePolicyManager::class.java)
            if (dpm != null) {
                // Dummy call to check if DevicePolicyManager is available and has permissions
                devicePolicyManager!!.setCameraDisabled(
                    ComponentName(
                        applicationContext.packageName.toString(),
                        DevicePolicyManager.EXTRA_DEVICE_ADMIN
                    ), true
                )

                return true
            } else {
                return false
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "hasDeviceAdminPrivilegesSecurityException: " + e.message)
            return false
        }
    }

    private fun disableRebootOptionR(context: Context) {
        try {
            devicePolicyManager!!.setCameraDisabled(
                ComponentName(
                    applicationContext.packageName.toString(),
                    DevicePolicyManager.EXTRA_DEVICE_ADMIN
                ), true
            ) // 0 disables the reboot option
        } catch (e: SecurityException) {
//            Log.e(TAG, "Failed to disable reboot option (R): " + e.message)
        }
    }

    private fun disableRebootOptionOreo(context: Context) {
        try {
            devicePolicyManager!!.setCameraDisabled(
                ComponentName(
                    applicationContext.packageName.toString(),
                    DevicePolicyManager.EXTRA_DEVICE_ADMIN
                ), true
            ) // 0 disables the reboot option
        } catch (e: SecurityException) {
//            Log.e(TAG, "Failed to disable reboot option (Oreo): " + e.message)
        }
    }

    private fun disableRebootOptionNougat(context: Context) {
        // This method is less reliable and may not work on all devices.
        try {
            devicePolicyManager!!.setCameraDisabled(
                ComponentName(
                    applicationContext.packageName.toString(),
                    DevicePolicyManager.EXTRA_DEVICE_ADMIN
                ), true
            ) // 0 disables the reboot option
        } catch (e: SecurityException) {
//            Log.e(TAG, "Failed to disable reboot option (Nougat): " + e.message)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getWholeInstallPackages() {
        val pm = packageManager

        //get a list of installed apps.
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val appList: List<ApplicationInfo> = packages
        val appString = appList.joinToString(separator = "\n") { it.toString() }
        val packageNames = appList.joinToString(separator = "\n") { it.packageName }
        PACKAGE_TO_SUSPEND = packageNames
        Log.d("SuspendApp", "getAllPackagesSuspended:  " + PACKAGE_TO_SUSPEND)
        Log.d("SuspendApp", "getAllPackagesSuspendedAppString:  " + appString)
        Log.d("SuspendApp", "getAllPackagesSuspendedPackageNames:  " + packageNames)

        /*val stringArray = arraySetOf<String>()
        for (packageInfo in packages) {
            stringArray.add(packageInfo.packageName)
            saveBlockedAppsToSharedPreferences(this, "MyPrefs", stringArray)

            Log.d("allPackages", "Installed package :" + packageInfo.packageName)
            Log.d(TAG, "Source dir : " + packageInfo.sourceDir)
            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName))
        }*/
    }

    private fun saveBlockedAppsToSharedPreferences(
        context: Context,
        key: String,
        blockedApps: Set<String>
    ) {
        // Get SharedPreferences instance
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Get an editor to put data
        val editor = sharedPreferences.edit()

        // Save the Set<String> into SharedPreferences
        editor.putStringSet(key, blockedApps)
        editor.apply()  // Apply changes asynchronously
    }

    private fun isMobileDataEnabled(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            val clazz = Class.forName(cm.javaClass.name)
            val method = clazz.getDeclaredMethod("getMobileDataEnabled")
            method.isAccessible = true // Make the method callable
            // get the setting for "mobile data"
            return method.invoke(cm) as Boolean
        } catch (e: Exception) {
            // Let it will be true by default
            return true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun lockDevice(context: Context) {
        devicePolicyManager =
            context.getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (devicePolicyManager != null) {
            try {
                devicePolicyManager!!.lockNow()
                resetPasswordWithToken()
                Log.d(TAG, "Device locked successfully")
            } catch (e: SecurityException) {
//                Log.e(TAG, "Lock failed: ${e.message}") //Handle the security exception
                //SecurityException typically means the application doesn't have the necessary permissions
            } catch (e: Exception) {
//                Log.e(TAG, "Lock failed: ${e.message}") //Handle other exceptions
            }
        } else {
            Log.e(TAG, "DevicePolicyManager not found")
        }
    }

//    @SuppressLint("NewApi")
//    fun changePasswordWithToken(context: Context) {
//
//        devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
//        val token: ByteArray? = generateRandomPasswordToken()
//        if (token == null) {
//            Log.d("tttttttttt1", "CheckTokenExp: " + "false")
//            Toast.makeText(context, "Error generating password token", Toast.LENGTH_SHORT).show()
//            return
//        }
////        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
//
//        if (devicePolicyManager == null) {
//            Log.d("tttttttttt1", "CheckDevicePolicyManagerExp: " + "false")
//            Toast.makeText(context, "Device Policy Manager not available", Toast.LENGTH_SHORT)
//                .show()
//            return
//        } else {
//            // Check if the device admin is enabled
//            if (devicePolicyManager.isAdminActive(mAdminComponent)) {
//                try {
//                    Log.d("tttttttttt1", "CheckIsDeviceAdminEnabled: " + "true")
//                    devicePolicyManager.setResetPasswordToken(mAdminComponent, token)
//                    devicePolicyManager.resetPasswordWithToken(mAdminComponent, "0000", token, 0) // Pass null for the old password
//
//                } catch (e: SecurityException) {
//                    Log.d(
//                        "tttttttttt1",
//                        "CheckIsDeviceAdminEnabledSecurityException: " + e.message.toString()
//                    )
//                } catch (e: Exception) {
//                    Log.d(
//                        "tttttttttt1",
//                        "CheckIsDeviceAdminEnabledException: " + e.message.toString()
//                    )
//                }
//                return
//            } else {
//                Log.d("tttttttttt1", "CheckIsDeviceAdminEnabled: " + "false")
//            }
//        }
//
//    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun resetPasswordWithToken() {
        val newPassword = "12345"
        val token = ByteArray(32)
        val random = SecureRandom()
        random.nextBytes(token)
        Log.d("tokenGet", "value is = ${token.contentToString()}")

// val token = byteArrayOf(4, 109, -59, -67, -2, -72, 110, -13, -49, 45, -122, 20, 51, -12, -14, -34, 49, 112, -100, 78, 123, -117, 72, -41, 101, -87, 17, 67, 69, -24, 77, -40)

        val success: Boolean = devicePolicyManager!!.setResetPasswordToken(mAdminComponent, token)


        devicePolicyManager!!.resetPasswordWithToken(componentName, newPassword, token, 0)
        Toast.makeText(this, "Password Reset and Device Locked", Toast.LENGTH_SHORT).show()
        Log.d("token", "value is = $success")

    }

    private fun generateRandomPasswordToken(): ByteArray? {
        try {
            val secureRandom = SecureRandom()  // Default secure random generator
            val token = ByteArray(32)  // Create a byte array of desired length
            secureRandom.nextBytes(token)  // Fill the byte array with random values
            return token
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun isDeviceOwner(context: Context): Boolean {
        val devicePolicyManager =
            context.getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val packageName = context.packageName // Your app's package name
        Log.d(TAG, "CheckpackageName: " + packageName)
        return devicePolicyManager.isDeviceOwnerApp(packageName)
    }

    // Function to disable the power button functionality at root level
    private fun disablePowerButton() {
        //Log.d(TAG, "CheckExpBtn : " + "bhar")
        try {
            // Executing root command to disable the power button's functionality
            val process = Runtime.getRuntime().exec("sh")
            val os = process.outputStream

            // Disable the power button by writing to the system file
            os.write("sh -c 'echo 0 > /sys/class/misc/power_button_state'\n".toByteArray())
            os.flush()
            os.close()

            // Wait for the command to finish execution
            process.waitFor()
        } catch (e: Exception) {
            Log.d(TAG, "CheckExpBtn : " + e.message.toString())
        }
    }

//    @SuppressLint("ServiceCast", "WakelockTimeout")
//    private fun disablePowerButtonNew(){
//        try {
//
//            val devicePolicyManager = applicationContext.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
//
//            devicePolicyManager.addUserRestriction(ComponentName(applicationContext, MyDeviceAdminReceiver::class.java), UserManager.DISALLOW_FACTORY_RESET)
//            // Initialize the WakeLock to keep the screen on
//            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
//            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.FULL_WAKE_LOCK, "YourApp::KeepScreenOn")
//
//            // Acquire the WakeLock
//            wakeLock.acquire()
//        } catch (e: Exception){
//            Log.d("tttchtach", "disablePowerButtonNew : " + e.message.toString())
//        }
//    }

    // Correctly defined inner class
    /*    inner class PowerButtonReceiver : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        // Screen is turning off (potential power button press)
                        // Do something here...
                        Toast.makeText(context, "Screen Off", Toast.LENGTH_SHORT).show()
                    }
                    Intent.ACTION_SCREEN_ON -> {
                        // Screen is turning on (potential power button press)
                        // Do something here...
                        Toast.makeText(context, "Screen On", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }*/

    /*    override fun onDestroy() {
            super.onDestroy()
            // Unregister the receiver
            unregisterReceiver(powerButtonReceiver)
        }*/

    /**
     * Disables the factory reset functionality via the power button
     * and keeps the screen on.
     *
     * @throws SecurityException if the app is not a device administrator.
     */
//    suspend fun disablePowerButtonResetAndKeepScreenOn() {
//        try {
//            val dpm = applicationContext.getSystemService(DevicePolicyManager::class.java)
//            if (dpm == null) {
//                Log.e("DeviceAdminReceiver", "DevicePolicyManager is null.")
//                return
//            }
//
//            val componentName = ComponentName(applicationContext.packageName, javaClass.name)
//
//            // Check if this app is already a device admin. If not, prompt the user.
//            if (dpm.getDevicePolicyManager().getDeviceAdminReceiver(javaClass.name) == null) {
//                Log.w("DeviceAdminReceiver", "Device admin not enabled. User authorization required.")
//                launch {
//                    showAdminAuthorizationIntent()
//                }
//                return // Exit the function if authorization is needed
//            }
//
//            // Disable factory reset.  Requires device administrator privilege.
//            dpm.addUserRestriction(componentName, UserManager.DISALLOW_FACTORY_RESET)
//
//            // Keep the screen on.
//            powerManager = applicationContext.getSystemService(PowerManager::class.java)
//            wakeLock = powerManager?.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "YourApp::KeepScreenOn")
//
//            wakeLock?.acquire()
//
//            Log.d("DeviceAdminReceiver", "Factory reset disabled and screen kept on.")
//
//        } catch (e: SecurityException) {
//            Log.e("DeviceAdminReceiver", "SecurityException: ${e.message}")
//            // Handle the case where the app is not a device administrator.
//        } catch (e: Exception) {
//            Log.e("DeviceAdminReceiver", "An error occurred: ${e.message}")
//        }
//    }

    // Function to disable the power button functionality at root level
    private fun disablePowerButtonNew() {
        try {
            // Executing root command to disable the power button's functionality
            val process = Runtime.getRuntime().exec("su")
            val os = process.outputStream
            // Disable the power button by writing to the system file
            os.write("echo 0 > /sys/class/misc/power_button_state\n".toByteArray())
            os.flush()
            os.close()
            // Wait for the command to finish execution
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    // Function to enable the power button functionality at root level
    private fun enablePowerButton() {
        try {
            // Executing root command to enable the power button's functionality
            val process = Runtime.getRuntime().exec("su")
            val os = process.outputStream
            // Enable the power button by writing to the system file
            os.write("echo 1 > /sys/class/misc/power_button_state\n".toByteArray())
            os.flush()
            os.close()
            // Wait for the command to finish execution
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val keyPressed: Int = event.keyCode
        if (keyPressed == KeyEvent.KEYCODE_POWER) {
            Log.d("###", "Power button long click")
            Toast.makeText(this@MainActivity, "Clicked: $keyPressed", Toast.LENGTH_SHORT).show()
            //send broadcast to close all dialogs
            //  sendBroadcast(Intent(Intent.))
            return true
        } else return super.dispatchKeyEvent(event)
    }


    fun hideSoftKeyboard(input: EditText) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input.windowToken, 0)
    }

    private fun showPopupWindow(unblockApps: AppCompatButton) {
        val popupMenu = PopupMenu(this, unblockApps)

        // Inflate a simple menu with 3 options
        menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        // Set a listener for item selection in the popup menu
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.option1 -> {
                    // Option 1 clicked
                    removeBlockedApp("com.android.chrome")
//                    Toast.makeText(this, "Google chrome selected", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.option2 -> {
                    // Option 2 clicked
                    removeBlockedApp("com.android.settings")
//                    Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.option3 -> {
                    // Option 3 clicked
                    removeBlockedApp("com.android.vending")
//                    Toast.makeText(this, "Play Store selected", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }

        // Show the popup menu
        popupMenu.show()
    }
}