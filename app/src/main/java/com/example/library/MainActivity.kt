package com.example.library

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.library.data.api.RetrofitInstance
import com.example.library.data.preferences.FavoritesManager
import com.example.library.data.repository.BookRepository
import com.example.library.navigation.BookExplorerNavigation
import com.example.library.ui.theme.LibraryTheme
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize dependencies
        val repository = BookRepository(RetrofitInstance.api)
        val favoritesManager = FavoritesManager(applicationContext)
        
        enableEdgeToEdge()
        setContent {
            LibraryTheme(
                darkTheme = isSystemInDarkTheme()
            ) {
                BookExplorerNavigation(
                    repository = repository,
                    favoritesManager = favoritesManager
                )
            }
        }
    }
}
