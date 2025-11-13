@echo off
REM Script to build and install the app on emulator

set ANDROID_HOME=C:\Users\thoka\AppData\Local\Android\Sdk
set PATH=%ANDROID_HOME%\platform-tools;%PATH%

echo ========================================
echo Building and Installing Unified HR System
echo ========================================

cd /d "%~dp0"

echo.
echo Step 1: Checking if emulator is running...
adb devices

echo.
echo Step 2: Building APK...
call gradlew.bat assembleDebug

if %ERRORLEVEL% NEQ 0 (
    echo Build failed! Please check the errors above.
    pause
    exit /b 1
)

echo.
echo Step 3: Installing APK on emulator...
adb install -r app\build\outputs\apk\debug\app-debug.apk

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS! App installed successfully!
    echo ========================================
    echo.
    echo Starting the app...
    adb shell am start -n com.unifiedhr.system/.ui.SplashActivity
    echo.
    echo App should now be running on the emulator!
) else (
    echo.
    echo Installation failed. Make sure emulator is running.
)

pause








