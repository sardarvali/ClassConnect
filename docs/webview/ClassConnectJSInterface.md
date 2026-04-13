# ClassConnectJSInterface.kt — JavaScript interface exposing native Android functions to a WebView

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/webview/ClassConnectJSInterface.kt`

---

## 🎯 What This File Does
`ClassConnectJSInterface` is a bridge class injected into `WebViewActivity`'s `WebView` via `addJavascriptInterface()`. It exposes two native Android functions — `saveToNotes()` and `showToast()` — that can be called from JavaScript running inside the web page with `window.ClassConnect.saveToNotes(text)` and `window.ClassConnect.showToast(message)`. Without this interface, web content loaded in `WebViewActivity` cannot call back into native Android code.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.content.Context` | Android SDK | App context | Needed to create Toast |
| `android.webkit.JavascriptInterface` | Android SDK | Annotation for JS-callable methods | Security annotation |
| `android.widget.Toast` | Android SDK | Short popup message | `showToast()` implementation |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `@JavascriptInterface`
**Critical security annotation.** Without this annotation, methods in a class added via `addJavascriptInterface()` are NOT accessible from JavaScript on Android 4.2+ (API 17+). Any method without this annotation is invisible to JS. This is a security measure — you must explicitly opt-in each method.

JavaScript calls: `window.ClassConnect.saveToNotes("some text")`
where `"ClassConnect"` is the name passed to `webView.addJavascriptInterface(interface, "ClassConnect")`.

### Security Warning — `addJavascriptInterface`
Adding a JavaScript interface to a WebView exposes native code to any JavaScript running in that WebView. If the WebView loads untrusted content (e.g., HTTP URLs), malicious JS could call these methods. Best practice:
- Only add to WebViews loading trusted HTTPS URLs.
- Only expose safe methods (no file system access, no database access).
- Both methods here are safe (Toast + notes stub).

### `text.take(50)`
Truncates the text to 50 characters in the Toast message. Prevents an extremely long string from overflowing the UI.

---

## 🏗️ Class Structure
`class ClassConnectJSInterface(private val context: Context)` — plain class, no DI, no singleton.

---

## ⚙️ Functions

### `saveToNotes(text: String)`
**Purpose:** (Placeholder) Saves web content text to user notes.
**Current implementation:** Shows a Toast: "Saved to notes: {first 50 chars}..."
**Future:** Should save to a Firestore notes collection or local Room database.
**Called from JavaScript as:** `window.ClassConnect.saveToNotes("text here")`

### `showToast(message: String)`
**Purpose:** Shows a native Android Toast from JavaScript.
**Current implementation:** `Toast.makeText(context, message, SHORT).show()`
**Called from JavaScript as:** `window.ClassConnect.showToast("Hello!")`

---

## ⚠️ Important Notes & Gotchas
- These methods run on the **JavaScript thread**, not the main (UI) thread. `Toast.makeText()` must be called on the main thread. The current code may crash on some devices. A safer implementation would use `Handler(Looper.getMainLooper()).post { Toast... }`.
- `saveToNotes` currently just shows a Toast — the actual notes saving feature is not yet implemented.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.webview

import android.content.Context
import android.webkit.JavascriptInterface
// @JavascriptInterface annotation — required for JS to be able to call this method.
import android.widget.Toast

class ClassConnectJSInterface(private val context: Context) {
// Injected into the WebView's JS scope via:
// webView.addJavascriptInterface(ClassConnectJSInterface(context), "ClassConnect")

    @JavascriptInterface
    // WITHOUT this annotation, JavaScript CANNOT call this method (API 17+).
    fun saveToNotes(text: String) {
        Toast.makeText(context, "Saved to notes: ${text.take(50)}...", Toast.LENGTH_SHORT).show()
        // text.take(50): prevents long text from overflowing the Toast.
        // TODO: Actually save to Firestore notes or local storage.
    }

    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        // JavaScript calls: window.ClassConnect.showToast("Hello from web!")
        // ⚠️ Runs on JS thread — may need runOnUiThread() on some devices.
    }
}
```

