package com.syed.classconnect.ui.webview

import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast

class ClassConnectJSInterface(private val context: Context) {
    @JavascriptInterface
    fun saveToNotes(text: String) {
        Toast.makeText(context, "Saved to notes: ${text.take(50)}...", Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

