package com.syed.classconnect.data.remote

data class GeminiRequest(val contents: List<GeminiContent>)
data class GeminiContent(val role: String = "user", val parts: List<GeminiPart>)
data class GeminiPart(val text: String)
data class GeminiResponse(val candidates: List<GeminiCandidate> = emptyList())
data class GeminiCandidate(val content: GeminiContent = GeminiContent(parts = emptyList()))

