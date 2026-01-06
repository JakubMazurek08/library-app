package com.example.library.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.library.data.model.Book
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("favorites")

class FavoritesManager(private val context: Context) {
    
    private val favoritesKey = stringSetPreferencesKey("favorite_books")
    private val favoriteBooksDataKey = stringPreferencesKey("favorite_books_data")
    private val gson = Gson()
    
    suspend fun getFavorites(): Set<String> {
        return context.dataStore.data.map { prefs ->
            prefs[favoritesKey] ?: emptySet()
        }.first()
    }
    
    suspend fun getFavoriteBooks(): List<Book> {
        return context.dataStore.data.map { prefs ->
            val json = prefs[favoriteBooksDataKey]
            if (json != null) {
                try {
                    val type = object : TypeToken<List<Book>>() {}.type
                    gson.fromJson<List<Book>>(json, type)
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }.first()
    }
    
    suspend fun addFavorite(book: Book) {
        context.dataStore.edit { prefs ->
            // Add to favorites set
            val currentIds = prefs[favoritesKey] ?: emptySet()
            prefs[favoritesKey] = currentIds + book.workId
            
            // Add book data - read from prefs directly to avoid deadlock
            val currentJson = prefs[favoriteBooksDataKey]
            val currentBooks = if (currentJson != null) {
                try {
                    val type = object : TypeToken<List<Book>>() {}.type
                    gson.fromJson<List<Book>>(currentJson, type).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
            } else {
                mutableListOf()
            }
            currentBooks.removeAll { it.workId == book.workId }
            currentBooks.add(book)
            prefs[favoriteBooksDataKey] = gson.toJson(currentBooks)
        }
    }
    
    suspend fun removeFavorite(bookId: String) {
        context.dataStore.edit { prefs ->
            // Remove from favorites set
            val currentIds = prefs[favoritesKey] ?: emptySet()
            prefs[favoritesKey] = currentIds - bookId
            
            // Remove book data - read from prefs directly to avoid deadlock
            val currentJson = prefs[favoriteBooksDataKey]
            val currentBooks = if (currentJson != null) {
                try {
                    val type = object : TypeToken<List<Book>>() {}.type
                    gson.fromJson<List<Book>>(currentJson, type).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
            } else {
                mutableListOf()
            }
            currentBooks.removeAll { it.workId == bookId }
            prefs[favoriteBooksDataKey] = gson.toJson(currentBooks)
        }
    }
    
    suspend fun isFavorite(bookId: String): Boolean {
        return getFavorites().contains(bookId)
    }
    
    suspend fun toggleFavorite(book: Book) {
        if (isFavorite(book.workId)) {
            removeFavorite(book.workId)
        } else {
            addFavorite(book)
        }
    }
}
