# Firebase Storage Security Rules — Complete Reference

---

## 📁 Location
`storage.rules` (project root)

---

## 🎯 What This File Does
Storage security rules control who can upload and download files in Firebase Cloud Storage. They enforce file size limits, allowed content types, and ownership-based access control.

---

## Rules by Path

### `/institutions/{instId}/{allPaths=**}` — Institution Files (Materials)
| Operation | Who Can Do It | Constraints |
|-----------|--------------|-------------|
| Read | Any authenticated user | None |
| Write | Any authenticated user | Max 50MB, must be image/*, application/pdf, or video/* |

### `/profile_photos/{uid}` — Profile Photos
| Operation | Who Can Do It | Constraints |
|-----------|--------------|-------------|
| Read | Any authenticated user | None |
| Write | Only the photo owner | Max 5MB, must be image/* |

The owner check uses: `request.auth.uid == uid.replace('.jpg', '')` because the file is stored as `{uid}.jpg`.

### `/submissions/{classId}/{assignmentId}/{studentId}` — Assignment Submissions
| Operation | Who Can Do It | Constraints |
|-----------|--------------|-------------|
| Read | Any authenticated user | None |
| Write | Only the submitting student | Max 50MB |

---

## Content Type Validation

```
request.resource.contentType.matches('image/.*')     // JPEG, PNG, GIF, etc.
request.resource.contentType == 'application/pdf'      // PDF only
request.resource.contentType.matches('video/.*')       // MP4, MOV, etc.
```

---

## ⚠️ Important Notes
- Deploy with: `firebase deploy --only storage`
- File size limits prevent abuse (50MB for materials/submissions, 5MB for photos)
- Content type validation prevents uploading executable files
- Read access is generous (any authenticated user) — fine for educational content
- Write access is restricted by ownership

