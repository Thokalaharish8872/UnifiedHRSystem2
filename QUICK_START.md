# Quick Start Guide - Run the App

## ✅ Emulator Status
Your emulator **Pixel_6_Pro** is now running!

## Next Steps to Run the App

### Option 1: Using Android Studio (Easiest)

1. **Open Android Studio**
   - Open the project: `C:\Users\thoka\StudioProjects\UnifiedHRSystem`

2. **Wait for Gradle Sync**
   - Android Studio will automatically sync Gradle files
   - Wait for "Gradle sync finished" message

3. **Select Emulator**
   - At the top toolbar, you should see "Pixel_6_Pro" in the device dropdown
   - If not visible, click the device dropdown and select it

4. **Run the App**
   - Click the green **Run** button (▶️) or press **Shift + F10**
   - Android Studio will:
     - Build the APK
     - Install it on the emulator
     - Launch the app automatically

### Option 2: Using Command Line (If Gradle Wrapper Exists)

If you have `gradlew.bat` in the project root:

```batch
REM Build the app
gradlew.bat assembleDebug

REM Install on emulator
adb install app\build\outputs\apk\debug\app-debug.apk

REM Launch app
adb shell am start -n com.unifiedhr.system/.ui.SplashActivity
```

### Option 3: Using the Batch Files I Created

1. **Start Emulator** (if not already running)
   - Double-click `start_emulator.bat`

2. **Build and Install**
   - Double-click `build_and_install.bat`
   - This will build and install the app automatically

## First Time Setup in App

Once the app launches:

1. **Register as Admin**
   - Enter your email (e.g., admin@company.com)
   - Enter password (at least 6 characters)
   - Click **"Register as Admin"**
   - Wait for "Admin account created successfully" message

2. **Create Company**
   - Click **"Create Company"** card
   - Enter company name
   - Click **"Create"**

3. **Add Managers** (Optional)
   - Click **"Manage Managers"**
   - Click **"Add Manager"**
   - Enter manager details

4. **Add Employees** (Optional)
   - Click **"Team Management"**
   - Click **"Add Team Member"**
   - Enter employee details

## Troubleshooting

### App won't build
- Make sure Firebase `google-services.json` is in `app` folder
- Sync Gradle: **File** → **Sync Project with Gradle Files**
- Clean project: **Build** → **Clean Project**

### App crashes on launch
- Check Logcat in Android Studio
- Make sure Firebase is configured
- Verify internet connection on emulator

### Emulator is slow
- Close other applications
- Increase emulator RAM in AVD settings
- Use a lower API level (API 30 instead of 33)

## Current Status

✅ Emulator: **Pixel_6_Pro** is running  
✅ Project: Located at `C:\Users\thoka\StudioProjects\UnifiedHRSystem`  
✅ Android SDK: Found at `C:\Users\thoka\AppData\Local\Android\Sdk`  

**Next:** Open Android Studio and click Run!








