/*© 2025 LS Creative Technologies. All rights reserved.
LS Creative Technologies is your strategic partner in innovation,
offering technology solutions that enhance performance,
streamline operations, and support long-term success.*/



package com.DevicePolicyManager.guardian.services

import android.accessibilityservice.AccessibilityService
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.PowerManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.DevicePolicyManager.guardian.recievers.MyDeviceAdminReceiver
import com.DevicePolicyManager.guardian.ui.BlockScreenActivity


class FocusModeService : AccessibilityService() {

    /*    private val blockedApps = mutableSetOf(
            "com.whatsapp",
            "com.facebook.katana",
            "com.instagram.android",
            "com.android.chrome",
            "com.android.camera",
            "com.android.vending",
            "com.android.settings",
            "com.example.accessibilitymonitor",
            "com.google.android.apps.messaging",
            "com.qti.confuridialer",
            "com.google.android.calendar",
            "com.google.android.apps.photos",
            "com.android.phone",
            "com.google.android.gm",
            "com.google.android.youtube",
            "com.google.android.apps.maps",
            "com.google.android.contacts",
            "com.google.android.music",
            "com.google.android.apps.youtube.music",
            "com.spotify.music",
            "com.huawei.camera",
            "com.huawei.photos",
            "com.huawei.email",
            "com.huawei.android.dialer",
            "com.huawei.contacts",
            "com.huawei.android.tms",
            "com.huawei.himaps",
            "com.motorola.settings",
            "com.motorola.gallery",
            "com.motorola.camera2",
            "com.motorola.contacts",
            "com.motorola.filemanager",
            "com.motorola.email",
            "com.alcatel.gallery",
            "com.alcatel.filemanager",
            "com.motorola.mobiledesktop.core",
            "com.motorola.android.launcher.overlay.koodo",
            "com.motorola.android.launcher.overlay.telus",
            "com.google.android.ext.services",
            "com.motorola.motocare",
            "com.android.providers.telephony",
            "com.android.dynsystem",
            "com.google.android.cellbroadcastservice",
            "com.motorola.revoker.services",
            "com.android.providers.calendar",
            "com.motorola.vzw.pco.extensions.pcoreceiver",
            "com.android.providers.media",
            "com.motorola.bug2go",
            "com.android.wallpapercropper",
            "com.google.android.federatedcompute",
            "com.motorola.gesture",
            "com.motorola.android.fmradio",
            "com.android.phone.overlay.motcommon",
            "com.android.cellbroadcast.overlay",
            "com.android.externalstorage",
            "com.motorola.omadm.service",
            "com.bluetooth.aptxmode",
            "com.qualcomm.qti.uceShimService",
            "com.android.companiondevicemanager",
            "com.android.mms.service",
            "com.android.providers.downloads",
            "com.motorola.coresettingsext",
            "vendor.qti.hardware.cacert.server",
            "com.motorola.enterprise.service",
            "com.motorola.att.phone.extensions",
            "com.android.phone.overlay.carriersettings",
            "com.qualcomm.qti.telephonyservice",
            "com.motorola.ccc.mainplm",
            "com.motorola.omadm.vzw",
            "vendor.qti.iwlan",
            "com.google.android.providers.media.module",
            "com.motorola.nfwlocationattribution",
            "com.android.systemui.plugin.globalactions.wallet",
            "com.android.providers.downloads.ui",
            "com.android.vending",
            "com.android.pacprocessor",
            "com.dolby.daxservice",
            "com.motorola.faceunlock",
            "com.google.android.adservices.api",
            "com.android.internal.systemui.navbar.hidegestural",
            "com.android.certinstaller",
            "com.android.carrierconfig",
            "com.qti.qualcomm.datastatusnotification",
            "com.qualcomm.wfd.service",
            "com.motorola.android.provisioning",
            "com.android.cellbroadcastservice.overlay",
            "com.motorola.msimsettings.overlay",
            "com.motorola.systemui.desk",
            "com.motorola.hiddenmenuapp",
            "com.google.android.sdksandbox",
            "com.android.egg",
            "com.android.mtp",
            "com.android.nfc",
            "com.android.ons",
            "com.android.stk",
            "com.android.backupconfirm",
            "org.codeaurora.ims",
            "com.android.wifi.resources.overlay.motCommon",
            "com.motorola.android.overlay.q com.motorola.android.overlay.qcom.common",
            "com.google.android.permissioncontroller",
            "com.qualcomm.qti.dynamicddsservice",
            "com.qualcomm.qti.xrvd.service",
            "com.qualcomm.qcrilmsgtunnel",
            "com.android.providers.settings",
            "com.android.sharedstoragebackup",
            "com.google.android.overlay.modules.ext.services",
            "com.android.se",
            "com.android.inputdevices",
            "com.google.android.apps.wellbeing",
            "com.motorola.android.nativedropboxagent",
            "com.motorola.nfc",
            "com.android.bips",
            "com.qti.dpmserviceapp",
            "com.google.android.modulemetadata",
            "com.android.cellbroadcastreceiver",
            "com.google.android.networkstack",
            "com.android.server.telecom",
            "com.motorola.android.providers.settings",
            "com.android.keychain",
            "com.android.service.ims",
            "com.google.android.packageinstaller",
            "com.google.android.gms",
            "com.google.android.gsf",
            "com.google.android.tts",
            "com.android.phone.overlay.common",
            "com.android.calllogbackup",
            "com.android.cameraextensions",
            "com.android.localtransport",
            "com.android.carrierdefaultapp",
            "com.qualcomm.qti.remoteSimlockAuth",
            "com.motorola.lifetimedata",
            "com.motorola.appdirectedsmsproxy",
            "com.android.proxyhandler",
            "com.android.providers.telephony.overlay.carriersettings",
            "com.motorola.thermalservice",
            "com.qualcomm.qti.workloadclassifier",
            "com.motorola.carrierconfig",
            "com.google.android.overlay.modules.permissioncontroller.forframework",
            "com.android.managedprovisioning",
            "com.android.soundpicker",
            "com.google.mainline.telemetry",
            "com.motorola.spectrum.setup.extensions",
            "com.motorola.paks",
            "com.motorola.dynamicvolume",
            "com.motorola.carriersettingsext",
            "com.motorola.freeform",
            "com.google.mainline.adservices",
            "com.android.wifi.resources.overlay.WifiVodafoneOverlay",
            "com.motorola.safetycenter.resources.overlay",
            "com.motorola.android.systemui.overlay.sprint",
            "com.motorola.dciservice",
            "com.google.android.gms.supervision",
            "com.android.storagemanager",
            "com.motorola.msimsettings",
            "com.qualcomm.qti.cne",
            "com.qualcomm.qti.uim",
            "com.google.android.overlay.modules.captiveportallogin.forframework",
            "com.qualcomm.location",
            "com.motorola.launcher3",
            "com.google.android.overlay.modules.modulemetadata.forframework",
            "com.qualcomm.qti.uimGbaApp",
            "com.android.vpndialogs",
            "com.android.phone",
            "com.android.shell",
            "com.android.wallpaperbackup",
            "com.android.providers.blockednumber",
            "com.android.providers.userdictionary",
            "com.android.emergency",
            "com.motorola.android.systemui.overlay.att",
            "com.motorola.android.systemui.overlay.tmo",
            "com.motorola.android.systemui.overlay.usc",
            "com.motorola.android.systemui.overlay.vzw",
            "com.qualcomm.qti.xrcb",
            "com.android.location.fused",
            "com.android.systemui",
            "com.google.android.ondevicepersonalization.services",
            "com.qti.phone",
    //        "com.DevicePolicyManager.guardian",
            "com.android.traceur",
            "com.motorola.discovery",
            "com.motorola.overlay.launcher3",
            "com.qti.qcc",
            "com.qualcomm.qtil.btdsda",
            "com.android.bluetooth",
            "com.qualcomm.timeservice",
            "com.qualcomm.atfwd",
            "com.qualcomm.embms",
            "com.android.providers.contacts",
            "vendor.qti.imsrcs",
            "com.factory.mmigroup",
            "com.google.android.ext.services",
            "com.qualcomm.qti.improvetouch.service",
            "com.android.providers.telephony",
            "com.android.dynsystem",
            "com.android.providers.calendar",
            "com.android.providers.media",
            "com.qti.service.colorservice",
            "com.android.wallpapercropper",
            "com.bsp.catchlog",
            "com.xiaomi.c ameratools",
            "com.wt.secret_code_manager",
            "com.android.externalstorage",
            "com.qualcomm.uimremoteclient",
            "com.factory.cit",
            "com.qualcomm.qti.uceShimService",
            "com.android.companiondevicemanager",
            "com.android.mms",
            "com.android.providers.downloads",
            "vendor.qti.hardware.cacert.server",
            "com.qualcomm.qti.telephonyservice",
            "com.android.partnerbrowsercustomizations.tmobile",
            "com.android.theme.icon_pack.circular.themepicker",
            "vendor.qti.iwlan",
            "com.qualcomm.uimremoteserver",
            "com.qti.confuridialer",
            "android.qvaoverlay.common",
            "com.android.providers.downloads.ui",
            "com.android.vending",
            "com.android.pacprocessor",
            "com.android.simappdialog",
            "android.overlay.common",
            "com.android.certinstaller",
            "com.android.carrierconfig",
            "com.google.android.marvin.talkback",
            "com.qti.qualcomm.datastatusnotification",
            "android",
            "com.qualcomm.qti.callfeaturessetting",
            "com.qualcomm.wfd.service",
            "com.qti.qualcomm.deviceinfo",
            "com.android.egg",
            "com.android.mtp",
            "com.android.nfc",
            "com.android.ons",
            "com.android.stk",
            "com.android.backupconfirm",
            "com.mmigroup.fmradio",
            "org.codeaurora.ims",
            "android.overlay.target",
            "com.qti.pasrservice",
            "com.google.android.permissioncontroller",
            "com.wingtech.setupwizardext",
            "com.qualcomm.qti.dynamicddsservice",
            "com.qualcomm.qcrilmsgtunnel",
            "com.android.providers.settings",
            "com.android.sharedstoragebackup",
            "com.qualcomm.qti.services.systemhelper",
            "com.android.wifi.resources.overlay.common",
            "com.google.android.overlay.modules.ext.services",
            "com.miui.bugreport",
            "com.android.se",
            "com.android.inputdevices",
            "com.google.android.apps.wellbeing",
            "com.qti.dpmserviceapp",
            "com.wing.wtsarcontrol",
            "com.qti.xdivert",
            "com.google.android.modulemetadata",
            "com.android.cellbroadcastreceiver",
            "com.qualcomm.qti.simsettings",
            "com.google.android.networkstack",
            "com.android.server.telecom",
            "com.android.cellbroadcastservice",
            "com.android.theme.icon_pack.rounded.themepicker",
            "com.android.keychain",
            "com.android.wifi.resources.overlay.target",
            "com.google.android.packageinstaller",
            "com.google.android.gms",
            "com.google.android.gsf",
            "com.google.android.tts",
            "com.google.android.gmsintegration",
            "com.android.phone.overlay.common",
            "com.qualcomm.qti.qtisystemservice",
            "com.android.carrierconfig.overlay.common",
            "com.android.calllogbackup",
            "com.android.systemui.overlay.common",
            "com.android.server.telecom.overlay.common",
            "com.android.localtransport",
            "com.android.carrierdefaultapp",
            "com.qualcomm.qti.remoteSimlockAuth",
            "com.qualcomm.qti.devicestatisticsservice",
            "com.android.proxyhandler",
            "com.sensetime.faceunlock",
            "com.qualcomm.qti.workloadclassifier",
            "com.miui.hybrid",
            "com.xiaomi.payment",
            "com.xiaomi.mipay.wallet.id",
            "com.xiaomi.mipay.wallet.in",
            "com.xiaomi.mirecycle",
            "com.xiaomi.milink.service",
            "com.xiaomi.glgm",
            "com.xiaomi.simactivate.service",
            "com.xiaomi.smarthome",
            "com.xiaomi.xmsf",
            "com.facebook.appmanager",
            "com.facebook.services",
            "com.facebook.system",
            "com.autonavi.minimap",
            "com.duokan.phone.remotecontroller",
            "com.duokan.phone.remotecontroller.peel.plugin",
            "com.netflix.partner.activation",
            "com.netflix.mediaclient",
            "com.opera.app.news",
            "com.opera.branding",
            "com.opera.mini.native",
            "com.opera.preinstall",
            "com.xiaomi.account",
            "com.xiaomi.gamecenter",
            "com.miui.contacts",
            "com.miui.mms",
            "com.miui.dialer",
            "com.xiaomi.mimanager",
            "com.miui.migration",
            "com.samsung.android.sms",
            "com.samsung.android.calendar",
            "com.samsung.android.memos",
            "com.samsung.android.camera.livephoto",
            "com.samsung.android.gallery",
            "com.samsung.android.music",
            "com.samsung.android.recovery",
            "com.samsung.android.bixby",
            "com.samsung.android.bixby.wakeup",
            "com.samsung.android.samsungpass",
            "com.samsung.android.samsungmembers",
            "com.samsung.android.app.smartthings",
            "com.samsung.android.app.sbrowser",
            "com.samsung.android.shealth",
            "com.samsung.android.widgetapp",
            "com.samsung.android.dreams.phototile",
            "com.samsung.android.app.galaxyfinder",
            "com.samsung.android.app.locksecret",
            "com.samsung.android.easysetup",
            "com.samsung.android.visionintelligence",
            "com.samsung.android.app.wellbeing",
            "com.samsung.android.smarttutor",
            "com.google.android.maps",
            "com.samsung.android.app.carrierportal",
            "com.samsung.android.app.callblocker",
            "com.samsung.android.app.samsungapps",
            "com.samsung.android.galaxyfinder",
            "com.samsung.android.smartcall",
            "com.samsung.android.ril",
            "com.samsung.android.omadm.agent",
            "com.samsung.android.providers.downloads",
            "com.samsung.android.app.notes",
            "com.samsung.android.galaxyapp",
            "com.samsung.android.app.tracker",
            "com.samsung.android.mdm",
            "com.samsung.android.app.contacts",
            "com.vivo.vms",
            "com.vivo.notes",
            "com.vivo.calendar",
            "com.vivo.music",
            "com.vivo.video",
            "com.vivo.speechinput",
            "com.vivo.fingerprint",
            "com.vivo.smartassistant",
            "com.vivo.smartshare",
            "com.vivo.cloud",
            "com.vivo.xplan",
            "com.vivo.findmyphone",
            "com.vivo.xiaomi.findmydevice",
            "com.vivo.appstore",
            "com.vivo.appmarket",
            "com.vivo.theme",
            "com.vivo.doswitcher",
            "com.vivo.quickreturn",
            "com.vivo.security",
            "com.vivo.securityservice",
            "com.vivo.zenmode",
            "com.vivo.smartmotion",
            "com.vivo.screenshot",
            "com.vivo.miaccount",
            "com.vivo.samsung.notes",
            "com.alibaba.alipay",
            "com.vivo.qq",
            "com.vivo.wechat",
            "com.nokia.contacts",
            "com.nokia.messaging",
            "com.nokia.gallery",
            "com.nokia.notes",
            "com.nokia.calendar",
            "com.nokia.clock",
            "com.nokia.music",
            "com.nokia.shares",
            "com.nokia.weather",
            "com.nokia.security",
            "com.nokia.search",
            "com.nokia.wifi",
            "com.nokia.migration",
            "com.nokia.nstore",
            "com.nokia.ovi",
            "com.nokia.device",
            "com.nokia.android.snake",
            "com.amazon.mShop.android.shopping",
            "com.microsoft.office",
            "com.microsoft.outlook",
            "com.microsoft.onedrive",
            "com.microsoft.launcher",
            "com.nokia.messenger",
            "com.lava.contacts",
            "com.lava.dialer",
            "com.lava.messaging",
            "com.lava.gallery",
            "com.lava.music",
            "com.lava.weather",
            "com.lava.calendar",
            "com.lava.notes",
            "com.lava.security",
            "com.lava.appstore",
            "com.lava.smartassistant",
            "com.lava.phoneoptimization",
            "com.lava.screenshot",
            "com.lava.phoneassistant",
            "com.lava.notificationmanager",
            "com.lava.securityservice",
            "com.lava.filemanager",
            "com.lava.smartfeature",
            "com.lava.batteryoptimizer",
            "com.lava.appguard",
            "com.lava.theme",
            "com.samsung.android.sbrowser",
            "com.linkedin.android",
            "com.oppo.dialer",
            "com.oppo.messaging",
            "com.oppo.camera",
            "com.oppo.music",
            "com.oppo.weather",
            "com.oppo.notes",
            "com.oppo.calendar",
            "com.oppo.screenshot",
            "com.oppo.security",
            "com.oppo.filemanager",
            "com.oppo.appmarket",
            "com.oppo.smartassistant",
            "com.oppo.flashlight",
            "com.oppo.wifi",
            "com.oppo.recovery",
            "com.oppo.backuprestore",
            "com.oppo.appmanager",
            "com.oppo.theme",
            "com.oppo.faceunlock",
            "com.oppo.fingerprint",
            "com.oppo.lockscreen",
            "com.oppo.dmt",
            "com.oppo.smoothscroll",
            "com.oppo.quickreturn",
            "com.oppo.vpn",
            "com.oppo.systemupdate",
            "com.oneplus.contacts",
            "com.oneplus.camera",
            "com.oneplus.music",
            "com.oneplus.notes",
            "com.oneplus.security",
            "com.oneplus.weather",
            "com.oneplus.calendar",
            "com.oneplus.screenrecorder",
            "com.oneplus.filemanager",
            "com.oneplus.gallery3d",
            "com.oneplus.smartspace",
            "com.oneplus.appmanager",
            "com.oneplus.theme",
            "com.oneplus.backup",
            "com.oneplus.jotter",
            "com.oneplus.optimized",
            "com.oneplus.gamemode",
            "com.oneplus.battery",
            "com.oneplus.deviceid",
            "com.oneplus.dashcharge",
            "com.oneplus.lockscreen",
            "com.oneplus.screenshot",
            "com.oneplus.widget",
            "com.oneplus.voiceassistant",
            "com.oneplus.smartassistant",
            "com.oneplus.fingerprint",
            "com.oneplus.faceunlock",
            "com.oneplus.applocker",
            "com.oneplus.dnd",
            "com.oneplus.shelf"
        )

        val allowedApps = arrayOf(
            "com.android.dialer",  // Calls
            "com.android.mms",  // SMS
            "com.android.chrome",  // Essential browsing (optional)
            "com.android.settings" // Settings (minimal)
        )


        // Method to remove a specific package from blockedApps
        fun removeBlockedApp(packageName: String) {
            if (blockedApps.contains(packageName)) {
                blockedApps.remove(packageName) // Removes the app from the set
                Toast.makeText(this, "$packageName has been unblocked.", Toast.LENGTH_SHORT).show()
            } else {
    //            Toast.makeText(
    //                this,
    //                "$packageName was not found in the blocked list.",
    //                Toast.LENGTH_SHORT
    //            ).show()
            }
        }*/

    fun getBlockedApps(context: Context): Set<String> {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return sharedPreferences.getStringSet("blockedApps", emptySet()) ?: emptySet()
    }

    private val shownToasts = mutableSetOf<String>()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()
            Log.d("tttttttttt", "blockedApp: $packageName")

            // Allow only our app
//            if (!packageName.equals(getPackageName())) {
//                val intent = Intent(this, BlockScreenActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//            }

//            for (pakg in getBlockedApps(this)) {
            if (getBlockedApps(this).contains(packageName)) {
//                if (!shownToasts.contains(packageName)) {
                Toast.makeText(
                    this,
                    "Your system blocked due to payment overdue.",
                    Toast.LENGTH_SHORT
                ).show()
//                    if (packageName != null) {
//                        shownToasts.add(packageName)
//                    }

//                    val intent = Intent(this, BlockScreenActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(intent)


                performGlobalAction(GLOBAL_ACTION_BACK) //Close the app immediately
                println("Blocked App: $packageName")
//                (applicationContext as MainActivity).showAccessibilityPrompt(this)
//                }
            } else {
                println("Allowed App: $packageName")
            }
//            }
        }
        //FEATURE-1:-, Code END

        //FEATURE-2:- This will disable opening to  app info if user want to uninstall the application from settings, Code STARTED
//        val packageName = event?.packageName.toString()
//        val className = event?.className.toString()
//        Log.d("uuuuuuu", "CheckBlockApps: $packageName")
//        if (packageName == "com.android.settings" && className.contains("InstalledAppDetails")) {
//            performGlobalAction(GLOBAL_ACTION_BACK) // Close the App Info screen
//            Toast.makeText(this, "You do not have permission to uninstall this application.", Toast.LENGTH_SHORT).show()
//        }
        //FEATURE-2:- Code ENDED
    }


    private fun lockScreen() {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (pm.isScreenOn) {
            val policy = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
            try {
                policy.lockNow()
            } catch (ex: SecurityException) {
                Toast.makeText(
                    this, """
                    You must enable this app as a device administrator
                    
                    Please enable it and press back button to return here.
                    """.trimIndent(),
                    Toast.LENGTH_LONG
                ).show()
                val admin = ComponentName(applicationContext, MyDeviceAdminReceiver::class.java)
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).putExtra(
                    DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    admin
                )
                applicationContext.startActivity(intent)
            }
        }

    }

    override fun onInterrupt() {}

    private fun getBlockedAppsFromSharedPreferences(context: Context, key: String): Set<String>? {
        // Get SharedPreferences instance
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Retrieve the Set<String> from SharedPreferences
        return sharedPreferences.getStringSet(
            key,
            null
        )  // Default value is null if key doesn't exist
    }
}
