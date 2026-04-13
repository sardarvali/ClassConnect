# ClassConnect — Setup Guide

## Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 11+
- Firebase account
- Google AI Studio account (for Gemini)
- NewsAPI account

---

## Step 1 — Clone / Open the Project
```
git clone https://github.com/sardarvali/ClassConnect
```
Open the `project/` folder in Android Studio.

---

## Step 2 — Create a Firebase Project
1. Go to https://console.firebase.google.com
2. Click **Add Project** → name it `ClassConnect`
3. Enable **Google Analytics** (optional)

### Enable Firebase Services:
| Service | Steps |
|---------|-------|
| **Authentication** | Build → Authentication → Get Started → Enable **Email/Password** and **Google** |
| **Cloud Firestore** | Build → Firestore Database → Create Database → Start in **production mode** → choose region |
| **Storage** | Build → Storage → Get Started → Start in **production mode** |
| **Cloud Messaging** | Enabled automatically |
| **Crashlytics** | Build → Crashlytics → Enable |

---

## Step 3 — Download google-services.json
1. In Firebase Console → Project Settings (gear icon) → **General**
2. Scroll to "Your apps" → Add **Android** app
3. Package name: `com.syed.classconnect`
4. App nickname: `ClassConnect`
5. Click **Register App**
6. Download `google-services.json`
7. Place it in `app/` directory:
   ```
   project/app/google-services.json
   ```

---

## Step 4 — Add SHA-1 for Google Sign-In
1. In Android Studio terminal:
   ```
   ./gradlew signingReport
   ```
2. Copy the **SHA-1** from the `debug` config
3. In Firebase Console → Project Settings → Your Android app → **Add fingerprint**
4. Paste SHA-1 and save

---

## Step 5 — Get Gemini API Key
1. Go to https://aistudio.google.com
2. Click **Get API Key** → **Create API Key**
3. Copy the key

---

## Step 6 — Get NewsAPI Key
1. Go to https://newsapi.org/register
2. Create a free account
3. Copy your API key from the dashboard

---

## Step 7 — Create local.properties
Add the following to `local.properties` in the project root (this file is gitignored):
```properties
# Android SDK path (already set by Android Studio)
sdk.dir=C\:\\Users\\<YourUser>\\AppData\\Local\\Android\\Sdk

# API Keys
gemini.api.key=YOUR_GEMINI_API_KEY_HERE
news.api.key=YOUR_NEWS_API_KEY_HERE
```

---

## Step 8 — Deploy Firestore Security Rules
1. Copy contents of `firestore.rules`
2. In Firebase Console → Firestore → **Rules** tab
3. Paste and click **Publish**

---

## Step 9 — Deploy Firebase Storage Rules
1. Copy contents of `storage.rules`
2. In Firebase Console → Storage → **Rules** tab
3. Paste and click **Publish**

---

## Step 10 — Create an Admin Institution
Since the app requires an institution code to register:
1. In Firebase Console → Firestore → **Start collection** → `institutions`
2. Add a document with:
   ```
   name: "Test Institution"
   adminId: ""         ← fill after first admin registers
   joinCode: "ADMIN1"  ← 6-char alphanumeric code
   createdAt: (current timestamp)
   ```
3. Users register with `ADMIN1` as the institution code
4. First user to register — manually set their `role` to `admin` and `isApproved` to `true` in Firestore

---

## Step 11 — Build Debug APK
```bash
./gradlew assembleDebug
```
APK output: `app/build/outputs/apk/debug/app-debug.apk`

---

## Step 12 — Create Keystore for Release
```bash
keytool -genkey -v -keystore classconnect.jks \
  -alias classconnect \
  -keyalg RSA -keysize 2048 \
  -validity 10000
```

Create `keystore.properties` in project root:
```properties
storeFile=classconnect.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=classconnect
keyPassword=YOUR_KEY_PASSWORD
```

Add to `app/build.gradle.kts` `android {}` block:
```kotlin
signingConfigs {
    create("release") {
        val keystoreProps = Properties()
        keystoreProps.load(rootProject.file("keystore.properties").inputStream())
        storeFile = file(keystoreProps["storeFile"] as String)
        storePassword = keystoreProps["storePassword"] as String
        keyAlias = keystoreProps["keyAlias"] as String
        keyPassword = keystoreProps["keyPassword"] as String
    }
}
```

---

## Step 13 — Build Release AAB (Play Store)
```bash
./gradlew bundleRelease
```
AAB output: `app/build/outputs/bundle/release/app-release.aab`

---

## Step 14 — Upload to Play Store
1. Go to https://play.google.com/console
2. Create new app → `ClassConnect`
3. Fill in store listing (description, screenshots, icon)
4. Go to **Testing → Internal testing** → Create release
5. Upload `app-release.aab`
6. Add SHA-1 of release keystore to Firebase
7. After testing, promote to **Production**

---

## Content Rating
- **ESRB:** Everyone (E)
- **Target audience:** 13+ (students and teachers)
- **Contains:** No violence, no adult content, educational platform

---

## Play Store Metadata

**Short description (80 chars):**
> Classroom collaboration: assignments, quizzes, attendance & AI study buddy

**Full description:**
> ClassConnect is a comprehensive classroom collaboration platform for students, teachers, and administrators.
>
> **For Students:**
> • Join classes with a 6-character code
> • Submit assignments and receive grades
> • Take quizzes with countdown timer
> • Mark attendance via QR code scan
> • Chat with classmates in real-time
> • AI Study Buddy powered by Gemini AI
>
> **For Teachers:**
> • Create and manage classes
> • Post announcements and materials
> • Create and grade assignments
> • Generate quizzes (manual or AI-generated)
> • Take attendance with QR code
> • AI Lesson Planner
>
> **For Administrators:**
> • Manage institution users
> • Approve teacher and student registrations
> • View class and user statistics
>
> **Key Features:**
> ✓ Real-time chat and announcements
> ✓ QR-based attendance with anti-proxy BLE verification
> ✓ Gemini AI integration for study help and lesson planning
> ✓ Dark mode support
> ✓ Offline browsing
> ✓ Push notifications
> ✓ Biometric app lock

---

## Troubleshooting

| Issue | Fix |
|-------|-----|
| `google-services.json not found` | Ensure file is in `app/` directory |
| `SHA-1 mismatch for Google Sign-In` | Add debug SHA-1 to Firebase |
| `Gemini API 401` | Check `gemini.api.key` in `local.properties` |
| `Institution code invalid` | Create an institution document in Firestore first |
| `Build fails: Hilt not found` | Run `./gradlew clean` then rebuild |

