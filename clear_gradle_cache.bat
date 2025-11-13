@echo off
REM Script to clear Gradle cache and fix JDK compatibility issues

echo ========================================
echo Clearing Gradle Cache
echo ========================================

echo.
echo Stopping Gradle daemon...
call gradlew.bat --stop 2>nul

echo.
echo Clearing Gradle transform cache...
if exist "%USERPROFILE%\.gradle\caches\transforms-3" (
    rmdir /s /q "%USERPROFILE%\.gradle\caches\transforms-3"
    echo Transform cache cleared!
) else (
    echo Transform cache not found.
)

echo.
echo Clearing project build folders...
if exist "app\build" (
    rmdir /s /q "app\build"
    echo Build folder cleared!
)

if exist ".gradle" (
    rmdir /s /q ".gradle"
    echo .gradle folder cleared!
)

echo.
echo ========================================
echo Cache cleared successfully!
echo ========================================
echo.
echo IMPORTANT: Configure Android Studio to use Java 17:
echo 1. File ^> Settings ^> Build Tools ^> Gradle
echo 2. Under "Gradle JDK", select Java 17
echo 3. If Java 17 is not available, download it from:
echo    https://adoptium.net/temurin/releases/?version=17
echo.
pause








