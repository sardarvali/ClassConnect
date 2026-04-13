# RegisterViewModel — See LoginViewModel (AuthViewModel)

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/auth/AuthViewModel.kt`

---

## 🎯 What This File Does
The project uses a single `AuthViewModel` for all auth screens. There is no separate `RegisterViewModel` file — registration logic is handled within `AuthViewModel`. See [LoginViewModel.md](LoginViewModel.md) for the complete documentation of `AuthViewModel`, including the `register()` function and `RegistrationResult` sealed class.

---

## 🔗 See Also
- [LoginViewModel.md](LoginViewModel.md) — Full AuthViewModel documentation
- [RegisterFragment.md](RegisterFragment.md) — The UI that uses the registration functions
- [AuthRepository.md](AuthRepository.md) — The repository functions called by register()

