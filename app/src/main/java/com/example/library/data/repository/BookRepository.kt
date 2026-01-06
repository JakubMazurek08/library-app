package com.example.library.data.repository

import com.example.library.data.api.OpenLibraryApi
import com.example.library.data.model.Book
import com.example.library.data.model.BookDetailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

class BookRepository(private val api: OpenLibraryApi) {
    
    suspend fun getDefaultBooks(limit: Int = 20, offset: Int = 0): ApiResult<List<Book>> {
        return searchBooks("bestseller", limit, offset)
    }
    
    suspend fun getBookDetails(workId: String): ApiResult<BookDetailResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getBookDetails(workId)
                if (response.isSuccessful && response.body() != null) {
                    ApiResult.Success(response.body()!!)
                } else {
                    ApiResult.Error("Failed to load book details: ${response.message()}")
                }
            } catch (e: Exception) {
                ApiResult.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }
    
    suspend fun searchBooks(query: String, limit: Int = 20, offset: Int = 0): ApiResult<List<Book>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.searchBooks(query, limit, offset)
                if (response.isSuccessful && response.body() != null) {
                    val books = response.body()!!.docs.map { it.toBook() }
                    ApiResult.Success(books)
                } else {
                    ApiResult.Error("Failed to search books: ${response.message()}")
                }
            } catch (e: Exception) {
                ApiResult.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }
}

