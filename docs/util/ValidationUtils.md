# ValidationUtils — Input validation helper functions

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/util/ValidationUtils.kt`

---

## 🎯 What This File Does
ValidationUtils is a Kotlin `object` (singleton) providing static validation functions for user input: email format, password strength, name sanitization, institution code format, and random code generation.

---

## ⚙️ Key Functions

### `isValidEmail(email: String): Boolean`
Uses `Patterns.EMAIL_ADDRESS.matcher(email).matches()` to validate email format.

### `isValidPassword(password: String): Boolean`
Checks password meets ALL requirements: 8+ chars, 1+ uppercase, 1+ digit, 1+ special char.

### `getPasswordStrength(password: String): PasswordStrength`
Returns an enum: `WEAK`, `MEDIUM`, `STRONG` based on which requirements are met.

### `sanitize(input: String, maxLength: Int = 100): String`
Trims whitespace and truncates to max length. Prevents excessively long input.

### `isValidInstitutionCode(code: String): Boolean`
Checks if code is exactly 6 alphanumeric characters.

### `generateCode(length: Int = 6): String`
Generates a random alphanumeric code of specified length (uppercase). Used for class codes and institution codes.

---

## ⚠️ Important Notes
- Password validation regex: `^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#\$%^&+=!])(?=\\S+\$).{8,}\$`
- `generateCode()` uses `('A'..'Z') + ('0'..'9')` character pool — no lowercase, no special chars
- `sanitize()` is called on all user text input before saving to Firestore

