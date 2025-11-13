# Setup Guide for Unified HR System

## Prerequisites

1. Android Studio (latest version)
2. JDK 8 or higher
3. Firebase account
4. Android device or emulator (API 24+)

## Step-by-Step Setup

### 1. Firebase Configuration

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a new project or select existing one
3. Add Android app:
   - Package name: `com.unifiedhr.system`
   - Download `google-services.json`
   - Place it in `app/` directory (replace the placeholder file)

### 2. Firebase Services Setup

#### Enable Authentication
1. Go to Firebase Console → Authentication
2. Enable "Email/Password" sign-in method
3. Save

#### Enable Realtime Database
1. Go to Firebase Console → Realtime Database
2. Create database (Start in test mode for development)
3. Note the database URL
4. Update security rules as needed

#### Enable Storage (Optional)
1. Go to Firebase Console → Storage
2. Get started with default rules
3. Update rules for production use

### 3. Android Studio Setup

1. Open the project in Android Studio
2. Sync Gradle files (File → Sync Project with Gradle Files)
3. Wait for dependencies to download
4. Build the project (Build → Make Project)

### 4. Run the Application

1. Connect Android device or start emulator
2. Click Run button or press Shift+F10
3. Select target device
4. Wait for app to install and launch

### 5. Initial Admin Setup

1. First launch will show Login screen
2. You need to create an admin account manually:
   - Go to Firebase Console → Authentication
   - Add user manually with email/password
   - Or modify LoginActivity to include registration

### 6. Database Rules (Firebase Realtime Database)

Update your database rules in Firebase Console:

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid || root.child('users').child(auth.uid).child('role').val() === 'Admin'",
        ".write": "$uid === auth.uid || root.child('users').child(auth.uid).child('role').val() === 'Admin'"
      }
    },
    "companies": {
      ".read": "auth != null",
      ".write": "auth != null && root.child('users').child(auth.uid).child('role').val() === 'Admin'"
    },
    "attendance": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "tasks": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "jobs": {
      ".read": "auth != null",
      ".write": "auth != null && (root.child('users').child(auth.uid).child('isRecruiter').val() === true || root.child('users').child(auth.uid).child('role').val() === 'Admin')"
    },
    "applicants": {
      ".read": "auth != null && (root.child('users').child(auth.uid).child('isRecruiter').val() === true || root.child('users').child(auth.uid).child('role').val() === 'Admin')",
      ".write": "auth != null && (root.child('users').child(auth.uid).child('isRecruiter').val() === true || root.child('users').child(auth.uid).child('role').val() === 'Admin')"
    },
    "performance": {
      ".read": "auth != null",
      ".write": "auth != null && (root.child('users').child(auth.uid).child('role').val() === 'Manager' || root.child('users').child(auth.uid).child('role').val() === 'Admin')"
    },
    "leaveRequests": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "expenses": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "documents": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "dailyReports": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "kras": {
      ".read": "auth != null",
      ".write": "auth != null && (root.child('users').child(auth.uid).child('role').val() === 'Manager' || root.child('users').child(auth.uid).child('role').val() === 'Admin')"
    }
  }
}
```

## Testing the Application

### Admin Testing
1. Login as Admin
2. Create company
3. Add managers
4. View all employees
5. Check performance dashboard

### Manager Testing
1. Login as Manager
2. Add team members
3. Create tasks
4. View team attendance
5. Post jobs (if recruiter enabled)

### Employee Testing
1. Login as Employee
2. Mark attendance
3. View tasks
4. Submit daily report
5. Apply for leave
6. Submit expense

## Troubleshooting

### Build Errors
- Ensure all dependencies are downloaded
- Check if `google-services.json` is in correct location
- Verify package name matches in manifest and Firebase

### Runtime Errors
- Check Firebase configuration
- Verify internet connection
- Check Firebase console for service status
- Review logcat for detailed error messages

### Permission Issues
- Ensure all permissions are granted on device
- Check AndroidManifest.xml for permission declarations
- For location: Enable location services on device

## Production Deployment

Before deploying to production:

1. Update Firebase security rules
2. Enable Firebase App Check
3. Set up proper authentication methods
4. Configure backup and recovery
5. Set up monitoring and analytics
6. Test on multiple devices
7. Optimize app performance
8. Review and update ProGuard rules

## Support

For issues during setup, check:
- Firebase documentation
- Android Studio documentation
- Project README.md








