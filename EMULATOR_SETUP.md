# Android Emulator Setup Guide

Since you cannot run Android Studio directly, here are the steps to create an emulator and run the app:

## Option 1: Using Android Studio GUI (Recommended)

1. **Open Android Studio**
   - Launch Android Studio
   - Open the project: `C:\Users\thoka\StudioProjects\UnifiedHRSystem`

2. **Create Emulator via Device Manager**
   - Click on **Tools** → **Device Manager** (or click the device icon in toolbar)
   - Click **Create Device** button
   - Select a device (e.g., **Pixel 5** or **Pixel 6**)
   - Click **Next**
   - Select a system image (e.g., **API 33** or **API 34** - Android 13/14)
   - Click **Download** if needed, then **Next**
   - Review settings and click **Finish**

3. **Start Emulator**
   - In Device Manager, click the **Play** button next to your emulator
   - Wait for emulator to boot (1-2 minutes)

4. **Run the App**
   - Once emulator is running, click the **Run** button (green play icon) in Android Studio
   - Or press **Shift + F10**
   - Select your emulator from the device list
   - App will build and install automatically

## Option 2: Using Command Line Scripts

I've created batch files to help you:

### Step 1: Create Emulator (if not exists)
- Double-click `create_emulator.bat`
- Follow instructions to create emulator via Android Studio

### Step 2: Start Emulator
- Double-click `start_emulator.bat`
- Wait for emulator to boot

### Step 3: Build and Install
- Double-click `build_and_install.bat`
- This will build the APK and install it on the emulator

## Option 3: Manual Command Line

If you prefer to run commands manually:

```batch
REM Set Android SDK path
set ANDROID_HOME=C:\Users\thoka\AppData\Local\Android\Sdk
set PATH=%ANDROID_HOME%\emulator;%ANDROID_HOME%\platform-tools;%PATH%

REM List available emulators
%ANDROID_HOME%\cmdline-tools\latest\bin\avdmanager.bat list avd

REM Start emulator (replace AVD_NAME with your emulator name)
%ANDROID_HOME%\emulator\emulator.exe -avd AVD_NAME

REM In another terminal, build the app
gradlew.bat assembleDebug

REM Install APK
adb install app\build\outputs\apk\debug\app-debug.apk

REM Launch app
adb shell am start -n com.unifiedhr.system/.ui.SplashActivity
```

## Troubleshooting

### Emulator won't start
- Make sure HAXM or Hyper-V is enabled
- Check if virtualization is enabled in BIOS
- Try a different system image (API 30 or lower)

### Build fails
- Make sure you've synced Gradle files in Android Studio
- Check that `google-services.json` is in the `app` folder
- Verify all dependencies are downloaded

### App crashes on launch
- Check logcat in Android Studio: **View** → **Tool Windows** → **Logcat**
- Make sure Firebase is properly configured
- Verify internet connection (for Firebase)

## Quick Start Checklist

- [ ] Android Studio is installed
- [ ] Project is opened in Android Studio
- [ ] Gradle files are synced
- [ ] Emulator is created
- [ ] Emulator is running
- [ ] Firebase `google-services.json` is configured
- [ ] App builds successfully
- [ ] App runs on emulator

## First Time Setup

1. **Register as Admin**
   - When app launches, enter email and password
   - Click "Register as Admin"
   - This creates your first admin account

2. **Create Company**
   - After login, click "Create Company"
   - Enter company name
   - Company profile is created

3. **Add Managers and Employees**
   - Use Admin dashboard to add managers
   - Managers can add team members

## Need Help?

If you encounter issues:
1. Check Android Studio's **Build** output for errors
2. Check **Logcat** for runtime errors
3. Verify Firebase configuration
4. Make sure emulator has internet connection








