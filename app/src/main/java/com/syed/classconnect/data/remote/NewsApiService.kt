package com.syed.classconnect.data.remote

import com.syed.classconnect.data.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/everything")
    suspend fun getEducationNews(
        @Query("domains") domains: String = "edsurge.com,edutopia.org,educationweek.org,eschoolnews.com,teachthought.com",
        @Query("q") query: String = "education OR teaching OR learning",
        @Query("language") language: String = "en",
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("pageSize") pageSize: Int = 10,
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse>
}

