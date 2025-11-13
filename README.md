# Unified HR System

A comprehensive Android application for Small & Medium Businesses (SMBs) to manage all HR operations in one unified system.

## Features

### 1. Onboarding Module
- Admin creates company profile
- Admin adds managers
- Managers add team members
- Automatic employee ID generation
- Role-based access control

### 2. Attendance & Daily Tracking
- Multiple attendance methods:
  - GPS-based location tracking
  - QR code scanning (office-based)
  - Web-based check-in
- Employee dashboard showing:
  - KRA (Key Result Areas)
  - Assigned tasks with deadlines
  - Daily progress reports
- End-of-day progress reporting

### 3. Work Allotment System
- Managers create and assign tasks
- Task status tracking (Pending, In Progress, Completed)
- Deadline management
- Manager dashboard with task overview
- Auto-reminders and nudges

### 4. Performance Management
- Monthly auto-generated scorecards:
  - Attendance percentage
  - Task completion percentage
  - Manager ratings (1-5 scale)
- Admin dashboard with:
  - Leaderboards
  - Performance graphs
  - Attrition risk flags

### 5. Recruitment Module
- Recruiter mode toggle (Admin/Manager)
- Job posting with unique job links
- Applicant pipeline management:
  - New → Shortlisted → Interview → Hired → Rejected
- One-click "Convert to Employee" functionality
- Automatic employee ID generation for hired candidates

### 6. Other HR Features
- **Leave Management**: Request and approval workflow
- **Expense Reimbursement**: Submit and track expenses
- **Documents Vault**: Digital storage for ID proofs, certificates (Google Drive integration ready)
- **Exit Flow**: Notice period and exit management
- **Role-based Permissions**: Admin, Manager, Employee roles
- **Audit Logs**: Track all system activities

## Technology Stack

- **Language**: Java
- **UI**: XML layouts
- **Backend**: Firebase
  - Firebase Authentication
  - Firebase Realtime Database
  - Firebase Storage
  - Firebase Firestore

## Project Structure

```
app/
├── src/main/
│   ├── java/com/unifiedhr/system/
│   │   ├── models/          # Data models
│   │   ├── services/        # Firebase service classes
│   │   ├── ui/              # Activities
│   │   │   └── fragments/   # Dialog fragments
│   │   ├── adapters/        # RecyclerView adapters
│   │   └── utils/           # Utility classes
│   ├── res/
│   │   ├── layout/          # XML layouts
│   │   ├── menu/           # Menu resources
│   │   └── values/         # Strings, colors, themes
│   └── AndroidManifest.xml
└── build.gradle
```

## Setup Instructions

1. **Firebase Setup**:
   - Create a Firebase project at https://console.firebase.google.com
   - Download `google-services.json` and place it in `app/` directory
   - Enable Firebase Authentication (Email/Password)
   - Enable Firebase Realtime Database
   - Enable Firebase Storage

2. **Build Configuration**:
   - Update `app/google-services.json` with your Firebase project details
   - Sync Gradle files
   - Build and run the application

3. **Initial Setup**:
   - First user should register as Admin
   - Admin creates company profile
   - Admin adds managers
   - Managers add team members

## Usage Flow

### Admin Flow
1. Login as Admin
2. Create company profile
3. Add managers
4. View all employees
5. Monitor performance and analytics
6. Manage recruitment

### Manager Flow
1. Login as Manager
2. View team members
3. Create and assign tasks
4. Monitor team attendance
5. Review performance
6. Post jobs (if recruiter mode enabled)

### Employee Flow
1. Login as Employee
2. Mark attendance (GPS/QR/Web)
3. View assigned tasks and KRAs
4. Submit daily progress reports
5. Apply for leave
6. Submit expense reimbursements
7. Access documents

## Database Structure

### Firebase Realtime Database Schema

```
{
  "users": {
    "userId": {
      "email", "name", "role", "companyId", 
      "employeeId", "managerId", "department", 
      "isRecruiter", "createdAt"
    }
  },
  "companies": {
    "companyId": {
      "companyName", "adminId", "address", 
      "employeeCount", "createdAt"
    }
  },
  "attendance": {
    "attendanceId": {
      "employeeId", "date", "checkInTime", 
      "checkOutTime", "location", "type"
    }
  },
  "tasks": {
    "taskId": {
      "title", "description", "assignedTo", 
      "assignedBy", "deadline", "status", "priority"
    }
  },
  "jobs": {
    "jobId": {
      "companyId", "title", "description", 
      "jobLink", "status", "createdBy"
    }
  },
  "applicants": {
    "applicantId": {
      "jobId", "name", "email", "resumeUrl", 
      "status", "appliedAt"
    }
  },
  "performance": {
    "performanceId": {
      "employeeId", "month", "year", 
      "attendancePercentage", "completionPercentage", 
      "rating", "managerNotes"
    }
  },
  "leaveRequests": {
    "leaveId": {
      "employeeId", "leaveType", "startDate", 
      "endDate", "reason", "status", "approvedBy"
    }
  },
  "expenses": {
    "expenseId": {
      "employeeId", "expenseType", "amount", 
      "description", "receiptUrl", "status", "approvedBy"
    }
  },
  "documents": {
    "documentId": {
      "employeeId", "documentType", "documentName", 
      "documentUrl", "googleDriveId"
    }
  },
  "dailyReports": {
    "reportId": {
      "employeeId", "date", "workDone", 
      "challenges", "nextDayPlan"
    }
  },
  "kras": {
    "kraId": {
      "employeeId", "title", "description", 
      "target", "currentProgress", "deadline"
    }
  }
}
```

## Permissions Required

- `INTERNET` - Network access
- `ACCESS_NETWORK_STATE` - Check network connectivity
- `ACCESS_FINE_LOCATION` - GPS-based attendance
- `ACCESS_COARSE_LOCATION` - Location services
- `CAMERA` - QR code scanning
- `READ_EXTERNAL_STORAGE` - Document access
- `WRITE_EXTERNAL_STORAGE` - Document storage

## Future Enhancements

- Push notifications for task reminders
- Advanced analytics and reporting
- Integration with payroll systems
- Mobile app for iOS
- Web dashboard
- API for third-party integrations
- Advanced document management with Google Drive API
- Biometric authentication
- Offline mode support

## License

This project is proprietary software for SMB HR management.

## Support

For issues and feature requests, please contact the development team.








