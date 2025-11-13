# Fix JDK Compatibility Error

## Problem
Java 25 is too new for Android Gradle Plugin 8.1.4. The `jlink.exe` process is failing.

## Solution 1: Configure Android Studio to Use Java 17 (Recommended)

1. **Download Java 17** (if not already installed):
   - Go to: https://adoptium.net/temurin/releases/?version=17
   - Download Java 17 LTS for Windows
   - Install it (e.g., to `C:\Program Files\Eclipse Adoptium\jdk-17.x.x`)

2. **Configure Android Studio**:
   - Open Android Studio
   - Go to **File** → **Settings** (or **Android Studio** → **Preferences** on Mac)
   - Navigate to **Build, Execution, Deployment** → **Build Tools** → **Gradle**
   - Under **Gradle JDK**, select **Download JDK...**
   - Choose **Version 17** and **Vendor: Eclipse Temurin**
   - Click **Download**
   - After download, select the downloaded JDK 17 from the dropdown
   - Click **Apply** and **OK**

3. **Update gradle.properties** (if needed):
   - The file already has Java toolchain configuration
   - If you know the exact path to Java 17, you can set:
     ```
     org.gradle.java.home=C:\\Program Files\\Eclipse Adoptium\\jdk-17.x.x
     ```

## Solution 2: Clear Gradle Cache

The cache might be corrupted. Clear it:

1. **Close Android Studio**

2. **Delete Gradle cache**:
   - Delete folder: `C:\Users\thoka\.gradle\caches`
   - Or just: `C:\Users\thoka\.gradle\caches\transforms-3`

3. **Delete build folders**:
   - In project: Delete `app\build` folder
   - Delete `.gradle` folder in project root (if exists)

4. **Reopen Android Studio** and sync

## Solution 3: Use Gradle Wrapper with Java 17

If you have Java 17 installed, you can set it in `gradle.properties`:

```properties
org.gradle.java.home=C:\\Path\\To\\Java17
```

## Quick Fix Script

Run this in PowerShell to clear caches:

```powershell
# Stop Gradle daemon
cd C:\Users\thoka\StudioProjects\UnifiedHRSystem
if (Test-Path "gradlew.bat") { .\gradlew.bat --stop }

# Clear Gradle cache
Remove-Item -Recurse -Force "$env:USERPROFILE\.gradle\caches\transforms-3" -ErrorAction SilentlyContinue

# Clear project build
Remove-Item -Recurse -Force "app\build" -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force ".gradle" -ErrorAction SilentlyContinue

Write-Host "Cache cleared! Now configure Android Studio to use Java 17."
```

## After Fixing

1. **Configure Android Studio to use Java 17** (Solution 1)
2. **Sync Gradle**: File → Sync Project with Gradle Files
3. **Clean and Rebuild**: Build → Clean Project, then Build → Rebuild Project

## Why This Happens

- Android Gradle Plugin 8.1.4 officially supports Java 17
- Java 25 is too new and has compatibility issues with `jlink` tool
- The Android SDK's `core-for-system-modules.jar` needs to be processed with a compatible JDK

## Recommended Java Versions for AGP 8.x

- **Java 17** (LTS) - ✅ Recommended
- **Java 11** (LTS) - ✅ Works but older
- **Java 21** (LTS) - ⚠️ May work but not officially supported
- **Java 25** - ❌ Too new, causes issues








