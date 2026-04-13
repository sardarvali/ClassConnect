# NetworkUtils.kt — Singleton object for connectivity checking and real-time network observation

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/util/NetworkUtils.kt`

---

## 🎯 What This File Does
`NetworkUtils` is a Kotlin `object` (singleton) providing three networking utilities: `isConnected()` for a one-shot synchronous connectivity check; `observeConnectivity()` which returns a `Flow<Boolean>` that reacts in real-time whenever the device goes online or offline (using `ConnectivityManager.NetworkCallback`); and `getConnectedSsid()` which returns the name of the currently-connected Wi-Fi network. Used by `MainActivity` or repository layers to show offline banners and gate API calls. Without it, the app would attempt network calls on an offline device, showing unhelpful error messages.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.content.Context` | Android SDK | App context | `getSystemService(CONNECTIVITY_SERVICE)` |
| `android.net.ConnectivityManager` | Android SDK | Network state service | Active network check + callbacks |
| `android.net.Network` | Android SDK | Represents one network (WiFi, cell) | Callback parameter |
| `android.net.NetworkCapabilities` | Android SDK | Network features | `NET_CAPABILITY_INTERNET` check |
| `android.net.NetworkRequest` | Android SDK | Filter for which networks to monitor | `registerNetworkCallback` |
| `android.net.wifi.WifiManager` | Android SDK | Wi-Fi service | `getConnectedSsid()` |
| `kotlinx.coroutines.channels.awaitClose` | Coroutines | Clean up callback when Flow is cancelled | Prevent listener leak |
| `kotlinx.coroutines.flow.Flow` | Coroutines | Cold observable stream | Return type of `observeConnectivity` |
| `kotlinx.coroutines.flow.callbackFlow` | Coroutines | Wrap callbacks in Flow | Convert NetworkCallback to Flow |
| `kotlinx.coroutines.flow.distinctUntilChanged` | Coroutines | Skip duplicate emissions | Don't re-notify if state didn't change |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `isConnected(context)` — synchronous check
```kotlin
val network = cm.activeNetwork ?: return false
val capabilities = cm.getNetworkCapabilities(network) ?: return false
return capabilities.hasCapability(NET_CAPABILITY_INTERNET)
```
Checks whether the currently active network has internet capability. Returns `false` for captive portals (connected to WiFi but no internet). Safe to call on the main thread (fast, no I/O).

### `observeConnectivity(context): Flow<Boolean>` — real-time
Wraps `ConnectivityManager.NetworkCallback` in a `callbackFlow`:
- `trySend(isConnected(context))` — emits current state immediately on collection
- `onAvailable(network)` → `trySend(true)` when any network connects
- `onLost(network)` → `trySend(false)` when last network disconnects
- `onUnavailable()` → `trySend(false)` if no network found after request
- `awaitClose { cm.unregisterNetworkCallback(callback) }` — removes listener when Flow is cancelled (prevents leak)
- `.distinctUntilChanged()` — suppresses duplicate `true`/`true` or `false`/`false` emissions

### `getConnectedSsid(context): String?`
Returns the SSID (network name) of the connected WiFi, with surrounding quotes stripped. Returns `null` if not on WiFi or if an error occurs. Used by `AttendanceBleService` to verify the student is on the same network as the teacher.

### `@Suppress("DEPRECATION")` on `wifiManager.connectionInfo`
`WifiManager.connectionInfo` is deprecated in API 31. The modern approach is `WifiManager.NetworkCallback`. Suppressed for backward compatibility with older Android versions.

---

## 🏗️ Class Structure
`object NetworkUtils` — singleton, no instances, no constructor.

---

## ⚙️ Functions

### `isConnected(context: Context): Boolean`
One-shot check: does the active network have internet capability?

### `observeConnectivity(context: Context): Flow<Boolean>`
Reactive stream: emits `true`/`false` whenever connectivity changes.

### `getConnectedSsid(context: Context): String?`
Returns current WiFi SSID. Returns `null` if not connected to WiFi.

---

## 🔄 Data Flow — observeConnectivity
```
MainActivity / Repository calls observeConnectivity(context)
        ↓
callbackFlow creates NetworkCallback + registers it with ConnectivityManager
        ↓
Immediately emits current state (isConnected)
        ↓
Device connects to WiFi → onAvailable() → emits true
Device loses WiFi → onLost() → emits false
        ↓
distinctUntilChanged() skips if value didn't change
        ↓
Caller shows/hides offline banner
        ↓
Flow cancelled (screen destroyed) → awaitClose removes callback → no leak
```

---

## ⚠️ Important Notes & Gotchas
- `isConnected()` returning `true` does NOT guarantee internet access — only that a network with `NET_CAPABILITY_INTERNET` exists. A captive portal (hotel WiFi requiring login) passes this check.
- `getConnectedSsid()` requires `ACCESS_WIFI_STATE` permission (granted in `AndroidManifest.xml`).
- `observeConnectivity()` must be collected in a lifecycle-aware scope (e.g., `repeatOnLifecycle`) to avoid collecting in the background.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

object NetworkUtils {

    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        // activeNetwork is null if no network at all.
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        // NET_CAPABILITY_INTERNET = this network is expected to reach the internet.
    }

    fun observeConnectivity(context: Context): Flow<Boolean> = callbackFlow {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(true) }
            // Network connected — trySend emits without throwing.
            override fun onLost(network: Network)      { trySend(false) }
            // Network disconnected.
            override fun onUnavailable()               { trySend(false) }
            // No network found matching the request.
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        trySend(isConnected(context))
        // Emit current state immediately — caller doesn't wait for next change.
        cm.registerNetworkCallback(request, callback)

        awaitClose { cm.unregisterNetworkCallback(callback) }
        // CRITICAL: remove the callback when the Flow is cancelled.
        // Without this, the callback fires forever → memory leak.
    }.distinctUntilChanged()
    // Skip duplicate emissions: online→online = one true, not two.

    fun getConnectedSsid(context: Context): String? {
        return try {
            val wifiManager = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            @Suppress("DEPRECATION")
            val info = wifiManager.connectionInfo
            // Deprecated in API 31 — kept for backward compatibility.
            info.ssid?.removeSurrounding("\"")
            // Android wraps SSID in quotes: "\"MyNetwork\"" → "MyNetwork"
        } catch (_: Exception) {
            null  // Not on WiFi, or permission missing.
        }
    }
}
```
