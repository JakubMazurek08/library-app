package com.example.library.data.model

import com.google.gson.annotations.SerializedName

data class BookListResponse(
    @SerializedName("works")
    val works: List<Book>
)

data class Book(
    @SerializedName("key")
    val key: String = "",
    
    @SerializedName("title")
    val title: String = "",
    
    @SerializedName("authors")
    val authors: List<Author>? = null,
    
    @SerializedName("cover_id")
    val coverId: Int? = null,
    
    @SerializedName("first_publish_year")
    val firstPublishYear: Int? = null,
    
    @SerializedName("edition_count")
    val editionCount: Int? = null
) {
    val workId: String
        get() = if (key.contains("/")) key.substringAfterLast("/") else key
    
    val coverUrl: String?
        get() = coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }
    
    val largeCoverUrl: String?
        get() = coverId?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" }
    
    val authorNames: String
        get() = authors?.joinToString(", ") { it.name } ?: "Unknown Author"
}

data class Author(
    @SerializedName("name")
    val name: String = ""
)

