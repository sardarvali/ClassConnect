# Project Setup Guide — ClassConnect

---

## 📁 Overview

This document walks you through setting up ClassConnect from scratch — cloning the repo, configuring Firebase, obtaining API keys, building, running, and deploying. Every step is documented with exact button names and paths.

---

## Prerequisites

| Tool | Required Version | Download |
|------|-----------------|---------|
| Android Studio | Hedgehog (2023.1.1) or newer | [developer.android.com/studio](https://developer.android.com/studio) |
| JDK | 11 (included with Android Studio) | Bundled |
| Git | Any recent version | [git-scm.com](https://git-scm.com) |
| Firebase Account | Free tier | [console.firebase.google.com](https://console.firebase.google.com) |
| Node.js + npm | 18+ (for Firebase CLI) | [nodejs.org](https://nodejs.org) |
| Firebase CLI | Latest | `npm install -g firebase-tools` |

---

## Step 1: Clone the Repository

```bash
git clone https://github.com/<your-username>/ClassConnect.git
cd ClassConnect
```

Open Android Studio → **File → Open** → select the `p2` folder (project root containing `build.gradle.kts`).

---

## Step 2: Create Firebase Project

1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Click **"Add project"**
3. Project name: `ClassConnect` → Click **Continue**
4. Google Analytics: Enable (optional) → **Continue**
5. Select or create an Analytics account → **Create project**
6. Wait for project creation → Click **Continue**

---

## Step 3: Register Android App in Firebase

1. In the Firebase Console, click the **Android icon** (Add app)
2. Android package name: `com.syed.classconnect`
3. App nickname: `ClassConnect`
4. Debug signing certificate SHA-1:
   - In Android Studio: **Gradle tab → :app → Tasks → android → signingReport**
   - Copy the `SHA1:` value
   - Paste into Firebase Console
5. Click **Register app**

---

## Step 4: Download google-services.json

1. After registering the app, click **Download google-services.json**
2. Place the file at: `app/google-services.json` (inside the `app/` directory, NOT the project root)
3. Click **Next** through the remaining steps in Firebase Console

---

## Step 5: Enable Authentication

1. In Firebase Console → **Build → Authentication**
2. Click **Get started**
3. **Sign-in method** tab:
   - Enable **Email/Password** → Toggle ON → **Save**
   - Enable **Google**:
     - Toggle ON
     - Set support email → your email
     - Click **Save**

---

## Step 6: Create Firestore Database

1. Firebase Console → **Build → Firestore Database**
2. Click **Create database**
3. **Location**: Choose the closest region (e.g., `asia-south1` for India, `us-central` for US)
4. **Security rules**: Start in **production mode** (we'll deploy custom rules later)
5. Click **Enable**

---

## Step 7: Enable Firebase Storage

1. Firebase Console → **Build → Storage**
2. Click **Get started**
3. Accept the default rules → **Next**
4. Choose the same region as Firestore → **Done**

---

## Step 8: Enable Firebase Cloud Messaging (FCM)

1. Firebase Console → **Project Settings** (gear icon) → **Cloud Messaging** tab
2. Ensure the Cloud Messaging API (V1) is **Enabled**
3. No further setup needed — the app handles FCM token registration automatically

---

## Step 9: Enable Firebase Crashlytics

1. Firebase Console → **Build → Crashlytics**
2. Click **Enable Crashlytics**
3. The app is already configured via the `firebase-crashlytics` Gradle plugin

---

## Step 10: Set Up API Keys

Create (or edit) `local.properties` in the project root directory. This file is already listed in `.gitignore` and will NOT be committed to version control.

```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
gemini.api.key=YOUR_GEMINI_API_KEY_HERE
news.api.key=YOUR_NEWS_API_KEY_HERE
```

### How to get a Gemini API Key:

1. Go to [aistudio.google.com](https://aistudio.google.com)
2. Sign in with your Google account
3. Click **"Get API key"** in the left sidebar
4. Click **"Create API key"**
5. Select your Google Cloud project (or create one)
6. Copy the generated API key
7. Paste it as the value for `gemini.api.key` in `local.properties`

### How to get a NewsAPI Key:

1. Go to [newsapi.org/register](https://newsapi.org/register)
2. Fill in: Name, Email, Password
3. Click **Submit**
4. Your API key will appear on the dashboard
5. Copy it and paste as the value for `news.api.key` in `local.properties`

---

## Step 11: Build and Run

1. In Android Studio, click **Sync Now** when prompted (or **File → Sync Project with Gradle Files**)
2. Wait for Gradle sync to complete (may take 2–5 minutes on first run)
3. Connect an Android device via USB or start an emulator (API 26+)
4. Click the green **Run ▶** button (or `Shift+F10`)
5. Select your device → Click **OK**
6. The app should build and launch on your device

---

## Step 12: Create First Admin Account

After the app runs for the first time:

1. Register a new account in the app (with any role)
2. Go to **Firebase Console → Firestore Database**
3. Navigate to the `users` collection
4. Find the document for your user (match by email)
5. Click the **pencil icon** to edit
6. Change the `role` field from `"student"` to `"admin"`
7. Change `isApproved` to `true`
8. Click **Update**
9. Log out of the app and log back in — you now have admin privileges

---

## Step 13: Deploy Security Rules

From the project root directory:

```bash
firebase login
firebase init  # Select Firestore and Storage, choose existing project
firebase deploy --only firestore:rules
firebase deploy --only storage
```

This deploys the rules defined in `firestore.rules` and `storage.rules` at the project root.

---

## Step 14: Build Release APK

### Create a Keystore:

```bash
keytool -genkey -v -keystore classconnect-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias classconnect
```

### Add signing config to `app/build.gradle.kts`:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../classconnect-release.jks")
            storePassword = "your_store_password"
            keyAlias = "classconnect"
            keyPassword = "your_key_password"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### Build the APK:

In Android Studio: **Build → Generate Signed Bundle / APK → APK → Next → Select keystore → Release → Create**

The APK will be at: `app/build/outputs/apk/release/app-release.apk`

---

## Common Errors and Solutions

| Error | Cause | Solution |
|-------|-------|---------|
| `google-services.json not found` | File missing or in wrong directory | Place `google-services.json` inside the `app/` directory |
| `SHA-1 not found` / Google Sign-In fails silently | SHA-1 fingerprint not added to Firebase | Run `signingReport` in Gradle, add SHA-1 to Firebase Console → Project Settings → Android app |
| `Gemini 404 error` | Wrong model name in API call | Ensure `GeminiApiService.kt` uses `gemini-2.0-flash` as the model name |
| `16KB page size warning` | Old CameraX version | Already updated to CameraX 1.4.1 in this project |
| `Hilt build error` | KSP/Hilt version mismatch | Ensure `ksp` and `hilt` versions match in `libs.versions.toml` |
| `Firebase Auth: USER_NOT_FOUND` | User deleted from Firebase Auth but Firestore doc exists | Delete the orphan Firestore document manually |
| `Task :app:kspDebugKotlin FAILED` | Generated code issue | Clean project (**Build → Clean Project**) then rebuild |
| App crashes on launch | Missing `@HiltAndroidApp` on Application class | Verify `ClassConnectApp.kt` has `@HiltAndroidApp` annotation |
| `Network error` on emulator | Emulator has no internet | Check emulator's WiFi settings, or use a physical device |

---

## Environment Variables Summary

| Variable | File | Purpose |
|----------|------|---------|
| `sdk.dir` | `local.properties` | Path to Android SDK |
| `gemini.api.key` | `local.properties` | Gemini AI API key for AI Buddy & Lesson Planner |
| `news.api.key` | `local.properties` | NewsAPI key for education news on home screen |

---

## Project Structure Quick Reference

```
p2/                          ← Project root
├── app/
│   ├── build.gradle.kts     ← App-level dependencies
│   ├── google-services.json ← Firebase config (DO NOT COMMIT)
│   ├── proguard-rules.pro   ← ProGuard rules for release builds
│   └── src/main/
│       ├── java/com/syed/classconnect/   ← All Kotlin source code
│       └── res/                           ← Layouts, drawables, values
├── build.gradle.kts          ← Project-level build config
├── gradle/
│   └── libs.versions.toml   ← Version catalog for all dependencies
├── firestore.rules           ← Firestore security rules
├── storage.rules             ← Storage security rules
├── firebase.json             ← Firebase project config
├── local.properties          ← API keys (git-ignored)
└── docs/                     ← This documentation folder
```

