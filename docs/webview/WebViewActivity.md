# WebViewActivity — In-app browser for URLs

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/webview/WebViewActivity.kt`

---

## 🎯 What This File Does
WebViewActivity provides an in-app browser for opening URLs without leaving the app. Used for news articles, external materials, and links. Features a toolbar with page title, back/forward navigation, and a "open in browser" option. Includes a JavaScript interface for ClassConnect-specific interactions.

---

## 📦 Imports

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `android.webkit.WebView` / `WebViewClient` / `WebSettings` | Android SDK | WebView components | In-app browser rendering |
| `android.content.Intent` / `Uri` | Android SDK | External browser | "Open in browser" fallback |
| `com.syed.classconnect.ui.webview.ClassConnectJSInterface` | App | JS bridge | Kotlin-to-JavaScript communication |

---

## ⚙️ Key Functions
- `onCreate()`: Configures WebView settings (JavaScript enabled, DOM storage, zoom controls)
- Sets WebViewClient to intercept page loads (keeps navigation in-app)
- Loads URL from intent extra `EXTRA_URL`
- Toolbar shows page title as it loads
- Back button: `webView.canGoBack()` → `webView.goBack()` else finish()

---

## ⚠️ Important Notes
- JavaScript is enabled (`settings.javaScriptEnabled = true`) — required for most modern web pages
- `ClassConnectJSInterface` provides a bridge for web pages to communicate back to the app
- WebView is destroyed in `onDestroy()` to prevent memory leaks

