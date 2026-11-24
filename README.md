# Battery Alarm
Native offline Kotlin Android app. Android 8–15 configuration (min 26, target 35).

## Build
Install Android Studio, JDK 17 and Android SDK 35. Open this folder as a project and sync with Gradle 8.9 (use a local Gradle installation if prompted; wrapper binaries are not included). Select Build > Generate App Bundles or APKs > Generate APKs. Debug APK is normally app/build/outputs/apk/debug/app-debug.apk. Initial dependency downloads require internet; the installed app does not.

## Phone setup
Install the APK, allowing installation from the chosen source if prompted. Open Battery Alarm, select thresholds and Start monitoring. Grant notifications. On Realme, allow background activity, unrestricted battery use and auto-launch where available. OEM restrictions can still interrupt monitoring. Force-stopping the app prevents automatic restart until reopened.

## Test
Set a threshold to trigger with the current charging state, then start monitoring. Check sound, dismiss, stop, and restart after reboot. Low alarm triggers while unplugged; high alarm while plugged in. Alerts rearm when the condition clears. The current battery label on the settings screen is a snapshot; the monitoring notification updates with battery broadcasts. Notification toggle controls alerts, not the required foreground notification. Dismiss using the app if notifications are denied. Alarm volume and Do Not Disturb may affect sound.

Source supplied without compilation or device testing. No APK or Gradle wrapper binaries included. Google Play distribution may require special-use foreground service review.

## Build automatically on GitHub (no Android Studio)
1. Create an empty repository at https://github.com/new.
2. Extract the project ZIP. Upload the contents to the repository root, including the hidden `.github` folder. If the upload interface omits it, create `.github/workflows/build-apk.yml` using Add file > Create new file and paste the supplied workflow content.
3. Commit the files. Open Actions > Build Battery Alarm APK. If necessary enable workflows and select Run workflow.
4. After the run succeeds, open it and download the BatteryAlarm-APK artifact. Extract the downloaded ZIP to get BatteryAlarm.apk.
5. Transfer to your phone, allow installation from the file manager when prompted, and install. Allow notifications and background activity, then start monitoring.

This produces a debug-signed APK. Separate hosted builds may use different debug signing keys; updating an existing installation may require uninstalling it first, which erases app settings. The workflow has not been executed in the preparation environment.
# BatteryAlarm
