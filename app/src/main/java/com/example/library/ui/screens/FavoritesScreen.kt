package com.example.library.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.library.data.model.Book
import com.example.library.data.preferences.FavoritesManager
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    favoritesManager: FavoritesManager,
    onBookClick: (Book) -> Unit,
    onNavigateBack: () -> Unit
) {
    // State management
    var favoriteBooks by remember { mutableStateOf<List<Book>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    // Function to load favorites
    fun loadFavorites() {
        scope.launch {
            isLoading = true
            error = null
            
            try {
                favoriteBooks = favoritesManager.getFavoriteBooks()
                isLoading = false
                error = null
            } catch (e: Exception) {
                isLoading = false
                error = "Failed to load favorites: ${e.message}"
            }
        }
    }
    
    // Load favorites when screen is displayed
    LaunchedEffect(Unit) {
        loadFavorites()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Favorites",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                isLoading -> {
                    LoadingView()
                }
                error != null && favoriteBooks.isEmpty() -> {
                    ErrorView(
                        message = error!!,
                        onRetry = { loadFavorites() }
                    )
                }
                favoriteBooks.isEmpty() -> {
                    EmptyFavoritesView()
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(favoriteBooks, key = { it.key }) { book ->
                            BookListItem(
                                book = book,
                                onClick = { onBookClick(book) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyFavoritesView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "💔",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "No Favorites Yet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Books you mark as favorites will appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

