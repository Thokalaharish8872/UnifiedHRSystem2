@echo off
REM Script to create and run Android Emulator for Unified HR System

set ANDROID_HOME=C:\Users\thoka\AppData\Local\Android\Sdk
set PATH=%ANDROID_HOME%\emulator;%ANDROID_HOME%\tools;%ANDROID_HOME%\tools\bin;%ANDROID_HOME%\platform-tools;%PATH%

echo ========================================
echo Creating Android Emulator
echo ========================================

REM Check if emulator already exists
echo Checking for existing emulators...
%ANDROID_HOME%\cmdline-tools\latest\bin\avdmanager.bat list avd

echo.
echo If no emulator exists, please create one using Android Studio:
echo 1. Open Android Studio
echo 2. Go to Tools ^> Device Manager
echo 3. Click "Create Device"
echo 4. Select a device (e.g., Pixel 5)
echo 5. Select a system image (e.g., API 33 or 34)
echo 6. Click Finish
echo.
echo Then run: start_emulator.bat

pause








