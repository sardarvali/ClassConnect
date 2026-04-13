# AIBuddyFragment — AI-powered study assistant chat

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/ai/AIBuddyFragment.kt`

---

## 🎯 What This File Does
AIBuddyFragment provides a chat interface where students can ask questions and get AI-powered explanations, practice problems, and study tips. Uses the Gemini 2.0 Flash API via GeminiRepository. Supports conversation history, markdown rendering, and clear conversation.

---

## ⚙️ Key Functions
- `onViewCreated()`: Sets up chat RecyclerView with AIChatAdapter, send button, clear button
- User sends message → `viewModel.sendMessage(userText)`
- AI responds → Markdown rendered via Markwon library
- Error handling for rate limits (429), network errors
- Conversation history maintained in AIViewModel for context-aware responses

---

## 🔄 Data Flow
```
User types question → taps send
    → AIViewModel.sendMessage(userText)
    → GeminiRepository.generateContent(history, prompt)
    → Retrofit → Gemini API → response text
    → AI message added to list → AIChatAdapter updates
    → Markwon renders markdown formatting
```

