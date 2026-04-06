# GeminiModels.kt — Data classes for Gemini API request and response serialization

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/data/remote/GeminiModels.kt`

---

## 🎯 What This File Does
This file contains the data classes that map the JSON structure of the Google Gemini API. `GeminiRequest` is sent to the API; `GeminiResponse` is received. The nesting matches the Gemini `generateContent` endpoint structure: a request has `contents` (a list of turns), each turn has a `role` and `parts`, each part has `text`. Retrofit uses Gson to serialize/deserialize these classes automatically. Without them, `GeminiApiService` would have no request/response types and Retrofit would not know how to build or parse the API payloads.

---

## 📦 Every Import — Explained
*(No imports — this file contains only data class declarations.)*

---

## 🔑 Every Keyword, Annotation & Concept Used

### `data class` — Auto-generated equals/hashCode/toString/copy
All classes here are `data class` because they are pure value objects representing JSON structures. Gson uses the field names directly for serialization (no `@SerializedName` needed since Kotlin field names match the API's JSON keys).

### Gemini API Request Structure
The `generateContent` endpoint expects:
```json
{
  "contents": [
    { "role": "user",  "parts": [{"text": "Hello"}] },
    { "role": "model", "parts": [{"text": "Hi there!"}] },
    { "role": "user",  "parts": [{"text": "What is AI?"}] }
  ]
}
```
This multi-turn format supports chat history — the entire conversation is sent with each request so Gemini has context.

### `role: String = "user"` — Default Parameter
`GeminiContent` has `role = "user"` as a default. When constructing AI responses locally (for history tracking), the role is overridden to `"model"`.

---

## 🏗️ Class Structure

| Class | Purpose | Fields |
|-------|---------|--------|
| `GeminiRequest` | Outer request wrapper | `contents: List<GeminiContent>` |
| `GeminiContent` | One conversation turn | `role: String`, `parts: List<GeminiPart>` |
| `GeminiPart` | One piece of a turn | `text: String` |
| `GeminiResponse` | Outer response wrapper | `candidates: List<GeminiCandidate>` |
| `GeminiCandidate` | One generated response | `content: GeminiContent` |

---

## 🔄 Data Flow Diagram
```
GeminiRepository.generateContent(history, prompt)
        ↓
Builds GeminiRequest(contents = history turns + new prompt)
        ↓
Retrofit serializes to JSON via Gson → POST to Gemini API
        ↓
Response JSON parsed into GeminiResponse
        ↓
response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
        ↓
AI text extracted → passed to ViewModel → displayed in AIChatAdapter
```

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.data.remote

data class GeminiRequest(val contents: List<GeminiContent>)
// Wrapper for the entire request. Contains all conversation turns.

data class GeminiContent(val role: String = "user", val parts: List<GeminiPart>)
// One conversation turn. role = "user" for human input, "model" for AI responses.
// Default role = "user" for convenience.

data class GeminiPart(val text: String)
// A text segment within a turn. In practice, each turn has exactly one part.

data class GeminiResponse(val candidates: List<GeminiCandidate> = emptyList())
// The API may return multiple candidates (response options). We use the first.
// Default emptyList() handles the case where the API returns no candidates.

data class GeminiCandidate(val content: GeminiContent = GeminiContent(parts = emptyList()))
// One candidate response. Contains a GeminiContent with role="model" and the AI text.
// Default empty content handles missing/null responses safely.
```

