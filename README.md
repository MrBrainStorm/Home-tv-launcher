# Home TV launcher

Personal Android TV home for Xiaomi MiTV-MSSP1 (Android 9), built from pinned CleanYourTV source.

- Four selected apps: KinoWatch, YouTube, Kinopoisk and VK Video; all apps remain available through the grid button.
- Eight bundled landscapes rotate every 30 minutes with a 20% shade. Credits are in assets/home-theme/CREDITS.md.
- Current weather for Kuleshovka, Rostov-on-Don and Azov comes from Open-Meteo, cached locally and refreshed on every entry to Home and every 30 minutes while Home remains visible; previous readings stay visible during requests and failures. Requests use the existing home SOCKS proxy at 172.31.250.2:10810.
- Theme files are installed into private application storage: this TV's missing adopted USB storage does not prevent wallpaper loading.
- Mi launches KinoWatch; voice launches YouTube through key mapping or the firmware assistant/search intent. HomeKeyService handles Home, stock-screen redirection and return to Home on wake.
- Exit opens the stock launcher temporarily. Home returns to this launcher; screen-on also returns to Home.

GitHub Actions checks JVM tests, Android lint and the release build. The uploaded APK is unsigned; sign locally with the same key for updates. Signing secrets and ADB keys must stay outside this repository.

Enable both accessibility helpers after installing (Android may remove them during replacement), and grant WRITE_SECURE_SETTINGS for helper restoration:

```sh
adb shell pm grant com.gathin.tvhome android.permission.WRITE_SECURE_SETTINGS
adb shell settings put secure enabled_accessibility_services com.gathin.tvhome/.HomeKeyService:com.gathin.tvhome/.InputKeyService
adb shell settings put secure accessibility_enabled 1
adb shell settings put secure assistant com.gathin.tvhome/.VoiceShortcutActivity
adb shell settings put secure voice_interaction_service ''
adb shell am start -n com.gathin.tvhome/.MainActivity
```

The stock launcher stays installed and enabled for Exit. Its priority on this firmware defeats Android's normal default-home preference, so HomeKeyService redirects its home screen instead.

The router has a narrow forward allow rule for this TV (192.168.92.135) to 172.31.250.2 TCP port 10810, before the diagnostic SOCKS restriction. Other clients keep their existing restriction.

Recovery:

```sh
adb shell pm enable --user 0 com.google.android.tvlauncher
adb shell settings delete secure enabled_accessibility_services
adb shell settings put secure accessibility_enabled 0
adb shell settings delete secure assistant
adb shell settings put secure voice_interaction_service com.google.android.katniss/.search.serviceapi.KatnissVoiceInteractionService
adb shell cmd package set-home-activity --user 0 com.google.android.tvlauncher/.MainActivity
adb shell am start -n com.google.android.tvlauncher/.MainActivity
```

Microphone handling runs entirely on the TV: a private app-generated ADB key connects only to 127.0.0.1:5555 and reads microphone key releases. Confirm the local debugging prompt once. No Pi service is used. ADB must remain enabled on the TV. AdbLib is vendored from cgutman/AdbLib at d6937951eb98557c76ee2081e383d50886ce109a; its license is included in assets.
