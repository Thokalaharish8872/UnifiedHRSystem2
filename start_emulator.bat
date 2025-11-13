@echo off
REM Script to start Android Emulator and run the app

set ANDROID_HOME=C:\Users\thoka\AppData\Local\Android\Sdk
set PATH=%ANDROID_HOME%\emulator;%ANDROID_HOME%\tools;%ANDROID_HOME%\tools\bin;%ANDROID_HOME%\platform-tools;%PATH%

echo ========================================
echo Starting Android Emulator
echo ========================================

REM List available AVDs
echo Available emulators:
%ANDROID_HOME%\cmdline-tools\latest\bin\avdmanager.bat list avd

echo.
echo Starting emulator...
REM Start emulator in background
start /B %ANDROID_HOME%\emulator\emulator.exe -avd Pixel_5_API_33 -no-snapshot-load

echo Waiting for emulator to boot (this may take 1-2 minutes)...
timeout /t 30

echo.
echo Checking if emulator is ready...
adb wait-for-device
adb shell getprop sys.boot_completed

echo.
echo Emulator should be ready now!
echo You can now build and run the app from Android Studio.
echo Or run: build_and_install.bat

pause








