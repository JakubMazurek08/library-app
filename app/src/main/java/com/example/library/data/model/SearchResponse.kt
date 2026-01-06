package com.example.library.data.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("docs")
    val docs: List<SearchBook>
)

data class SearchBook(
    @SerializedName("key")
    val key: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("author_name")
    val authorName: List<String>?,
    
    @SerializedName("cover_i")
    val coverId: Int?,
    
    @SerializedName("first_publish_year")
    val firstPublishYear: Int?,
    
    @SerializedName("edition_count")
    val editionCount: Int?,
    
    @SerializedName("number_of_pages_median")
    val numberOfPages: Int?
) {
    val workId: String
        get() = key.substringAfterLast("/")
    
    val coverUrl: String?
        get() = coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }
    
    val largeCoverUrl: String?
        get() = coverId?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" }
    
    val authorNames: String
        get() = authorName?.joinToString(", ") ?: "Unknown Author"
    
    fun toBook(): Book {
        return Book(
            key = key,
            title = title,
            authors = authorName?.map { Author(name = it) },
            coverId = coverId,
            firstPublishYear = firstPublishYear,
            editionCount = editionCount
        )
    }
}

