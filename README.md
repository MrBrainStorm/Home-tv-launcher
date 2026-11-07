# Home TV launcher

Personal Android TV home for Xiaomi MiTV-MSSP1 (Android 9), built from pinned CleanYourTV source.

- Four selected apps: KinoWatch, YouTube, Kinopoisk and VK Video; all apps remain available through the grid button.
- Eight bundled landscapes rotate every 30 minutes with a 20% shade. Credits are in assets/home-theme/CREDITS.md.
- Current weather for Kuleshovka, Rostov-on-Don and Azov comes from Open-Meteo, cached locally and refreshed every 30 minutes while Home is visible. Requests use the existing home SOCKS proxy at 172.31.250.2:10810.
- Theme files are installed into private application storage: this TV's missing adopted USB storage does not prevent wallpaper loading.
- Mi launches KinoWatch; voice launches YouTube. HomeKeyService handles Home, stock-screen redirection and return to Home on wake.
- Exit opens the stock launcher temporarily. Home returns to this launcher; screen-on also returns to Home.

GitHub Actions checks JVM tests, Android lint and the release build. The uploaded APK is unsigned; sign locally with the same key for updates. Signing secrets and ADB keys must stay outside this repository.

Enable both accessibility helpers after installing (Android may remove them during replacement), and grant WRITE_SECURE_SETTINGS for helper restoration:

```sh
adb shell pm grant com.gathin.tvhome android.permission.WRITE_SECURE_SETTINGS
adb shell settings put secure enabled_accessibility_services com.gathin.tvhome/.HomeKeyService:com.gathin.tvhome/.InputKeyService
adb shell settings put secure accessibility_enabled 1
adb shell am start -n com.gathin.tvhome/.MainActivity
```

The stock launcher stays installed and enabled for Exit. Its priority on this firmware defeats Android's normal default-home preference, so HomeKeyService redirects its home screen instead.

Recovery:

```sh
adb shell pm enable --user 0 com.google.android.tvlauncher
adb shell settings delete secure enabled_accessibility_services
adb shell settings put secure accessibility_enabled 0
adb shell cmd package set-home-activity --user 0 com.google.android.tvlauncher/.MainActivity
adb shell am start -n com.google.android.tvlauncher/.MainActivity
```
