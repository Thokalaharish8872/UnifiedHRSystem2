# Unified HR System

A comprehensive Android application for Small & Medium Businesses (SMBs) to manage all HR operations in one unified system.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [Usage Flow](#usage-flow)
- [Database Structure](#database-structure)
- [Permissions Required](#permissions-required)
- [License](#intention-of-the-application)

## Features

The application is divided into several modules to cover all aspects of the HR lifecycle:

### 1. Onboarding Module
- Admin creates a company profile.
- Admin onboards managers.
- Managers add their respective team members.
- Employee IDs are generated automatically upon creation.
- Role-based access control ensures users only see what they need to.

### 2. Attendance & Daily Tracking
- **Multiple Attendance Methods**: GPS-based location, QR code scanning for office-based staff, and web-based check-in.
- **Employee Dashboard**: A central hub for employees to view their Key Result Areas (KRAs), assigned tasks with deadlines, and daily progress reports.
- **End-of-Day Reporting**: A simple flow for employees to submit their daily progress.

### 3. Work Allotment System
- Managers can create and assign tasks to their team members.
- Real-time task status tracking (Pending, In Progress, Completed).
- Deadline management for all tasks.
- Manager-specific dashboard with an overview of all team tasks.
- Automatic reminders and nudges for upcoming deadlines.

### 4. Performance Management
- Monthly auto-generated scorecards based on:
  - Attendance Percentage
  - Task Completion Rate
  - Manager Ratings (on a 1-5 scale)
- Admin dashboard with leaderboards, performance graphs, and attrition risk flags.

### 5. Recruitment Module
- **Recruiter Mode**: Admins and Managers can be granted recruiter permissions to post jobs.
- **Job Postings**: Create job openings with unique, shareable links.
- **Applicant Pipeline**: Manage applicants through a simple pipeline (New → Shortlisted → Interview → Hired → Rejected).
- **One-Click Onboarding**: Convert a `Hired` applicant into an employee profile with a single click, automatically generating their credentials.

### 6. Other HR Features
- **Leave Management**: A complete workflow for leave requests and approvals.
- **Expense Reimbursement**: A system for employees to submit and track expenses.
- **Documents Vault**: Digital storage for ID proofs, certificates, and other documents.
- **Exit Flow**: Formal management of the notice period and exit process.
- **Audit Logs**: Track all major activities within the system for accountability.

## Technology Stack

- **Language**: Java
- **UI**: XML
- **Backend**: Firebase
  - Firebase Authentication
  - Firebase Realtime Database

## Project Structure

```
app/
├── src/main/
│   ├── java/com/unifiedhr/system/
│   │   ├── models/          # Data Models (POJOs)
│   │   ├── services/        # Firebase Service Classes
│   │   ├── ui/              # Activities & Fragments
│   │   ├── adapters/        # RecyclerView Adapters
│   │   └── utils/           # Utility and Helper Classes
│   ├── res/
│   │   ├── layout/          # XML Layouts
│   │   ├── menu/            # Menu XML Files
│   │   └── values/          # Strings, Colors, Styles
│   └── AndroidManifest.xml
└── build.gradle
```

## Setup Instructions

1.  **Firebase Setup**:
    -   Create a new Firebase project at [console.firebase.google.com](https://console.firebase.google.com).
    -   In your project, create an Android app with the package name `com.unifiedhr.system`.
    -   Download the `google-services.json` file and place it in the `app/` directory of your project.
    -   Enable **Email/Password** sign-in in the Firebase Authentication section.
    -   Enable the **Realtime Database**.
    -   Enable **Firebase Storage**.

2.  **Build Configuration**:
    -   Open the project in Android Studio.
    -   Sync the Gradle files to download all dependencies.
    -   Build and run the application on an emulator or physical device.

3.  **Initial Setup**:
    -   The first user to register must be the **Super Admin**.
    -   The Super Admin can then approve registration requests from **Admins**.
    -   Admins create a company profile and add **Managers**.
    -   Managers add their **Employees**.

## Usage Flow

#### Admin Flow
1.  Login as an Admin.
2.  Manage company profile and settings.
3.  Add and manage managers.
4.  View all employees and teams in the organization.
5.  Monitor company-wide performance and analytics.
6.  Oversee recruitment.

#### Manager Flow
1.  Login as a Manager.
2.  View and manage direct team members.
3.  Create, assign, and track tasks.
4.  Approve/reject attendance and leave requests from team members.
5.  Review team performance.

#### Employee Flow
1.  Login as an Employee.
2.  Mark daily attendance.
3.  View assigned tasks and KRAs.
4.  Submit daily progress reports.
5.  Apply for leave and submit expenses.

## Database Structure

### Firebase Realtime Database Schema

```json
{
  "users": {
    "userId": {
      "email": "String",
      "name": "String",
      "role": "String",
      "companyId": "String",
      "employeeId": "String",
      "managerId": "String",
      "department": "String",
      "isRecruiter": "boolean",
      "createdAt": "long"
    }
  },
  "companies": {
    "companyId": {
      "companyName": "String",
      "adminId": "String",
      "address": "String",
      "employeeCount": "int",
      "createdAt": "long"
    }
  },
  "attendance": {
    "attendanceId": {
      "employeeId": "String",
      "date": "String",
      "checkInTime": "long",
      "checkOutTime": "long",
      "status": "String",
      "reason": "String"
    }
  },
  "tasks": {
    "taskId": {
      "title": "String",
      "description": "String",
      "assignedTo": "String",
      "assignedBy": "String",
      "deadline": "String",
      "status": "String"
    }
  },
  "jobs": {
    "jobId": {
      "companyId": "String",
      "companyName": "String",
      "title": "String",
      "description": "String",
      "status": "String",
      "createdBy": "String"
    }
  }
}
```

## Permissions Required

-   `INTERNET`
-   `ACCESS_NETWORK_STATE`
-   `ACCESS_FINE_LOCATION`
-   `ACCESS_COARSE_LOCATION`
-   `CAMERA`
-   `READ_EXTERNAL_STORAGE`
-   `WRITE_EXTERNAL_STORAGE`

## intention of the application

This project is proprietary software intended for HR management.
