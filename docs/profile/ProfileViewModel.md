# ProfileViewModel — Profile state management

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/profile/ProfileViewModel.kt`

---

## 🎯 What This File Does
ProfileViewModel manages user profile state: loading user data, updating profile fields, uploading profile photos. Uses AuthRepository for user data and StorageRepository for photo uploads.

---

## ⚙️ Key Functions
- `loadProfile(uid)`: Fetches user document from Firestore
- `updateProfile(uid, updates: Map)`: Updates user fields (name, bio)
- `uploadPhoto(uid, uri)`: Uploads photo to Storage, updates photoUrl in Firestore
- `logout()`: Signs out from Firebase Auth

