# TeacherHomeViewModel — See HomeViewModel

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/home/HomeViewModel.kt`

---

The project uses a single shared `HomeViewModel` for both student and teacher home screens. `TeacherHomeViewModel` is not a separate class.

See **[HomeViewModel.md](HomeViewModel.md)** for complete documentation.

### Teacher-specific functions in HomeViewModel

| Function | What it does |
|----------|-------------|
| `loadTeacherHomeData(uid)` | Loads today's classes for the teacher + news articles |
| `loadNews()` | Fetches education news from NewsAPI; falls back to 4 hardcoded articles |
| `getDayName()` | Returns English day name for schedule matching |

The teacher home data loads three parallel coroutines:
1. Unread notification count
2. Today's taught classes (filtered from all teacher classes by day name)
3. News articles from NewsAPI
