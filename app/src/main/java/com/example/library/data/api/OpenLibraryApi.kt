package com.example.library.data.api

import com.example.library.data.model.BookDetailResponse
import com.example.library.data.model.BookListResponse
import com.example.library.data.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenLibraryApi {
    
    @GET("works/{work_id}.json")
    suspend fun getBookDetails(
        @Path("work_id") workId: String
    ): Response<BookDetailResponse>
    
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<SearchResponse>
}

