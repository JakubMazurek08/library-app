package com.example.library.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.library.data.model.Book
import com.example.library.data.model.BookDetailResponse
import com.example.library.data.preferences.FavoritesManager
import com.example.library.data.repository.ApiResult
import com.example.library.data.repository.BookRepository
import kotlinx.coroutines.launch

@Composable
fun BookDetailScreen(
    book: Book,
    repository: BookRepository,
    favoritesManager: FavoritesManager,
    onNavigateBack: () -> Unit
) {
    // State management
    var bookDetail by remember { mutableStateOf<BookDetailResponse?>(null) }
    var isFavorite by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    // Load book details and favorite status
    LaunchedEffect(book.workId) {
        isLoading = true
        error = null
        
        // Check if book is favorite
        isFavorite = favoritesManager.isFavorite(book.workId)
        
        // Load book details
        when (val result = repository.getBookDetails(book.workId)) {
            is ApiResult.Success -> {
                bookDetail = result.data
                isLoading = false
                error = null
            }
            is ApiResult.Error -> {
                isLoading = false
                error = result.message
            }
            is ApiResult.Loading -> {}
        }
    }
    
    // Toggle favorite function
    fun toggleFavorite() {
        scope.launch {
            favoritesManager.toggleFavorite(book)
            isFavorite = !isFavorite
        }
    }
    
    // Retry function
    fun retry() {
        scope.launch {
            isLoading = true
            error = null
            when (val result = repository.getBookDetails(book.workId)) {
                is ApiResult.Success -> {
                    bookDetail = result.data
                    isLoading = false
                    error = null
                }
                is ApiResult.Error -> {
                    isLoading = false
                    error = result.message
                }
                is ApiResult.Loading -> {}
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val favoriteIconColor by animateColorAsState(
            targetValue = if (isFavorite) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.onSurface,
            label = "Favorite icon color"
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
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
                    text = "Book Details",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            IconButton(onClick = { toggleFavorite() }) {
                Icon(
                    imageVector = if (isFavorite) 
                        Icons.Default.Favorite 
                    else 
                        Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) 
                        "Remove from favorites" 
                    else 
                        "Add to favorites",
                    tint = favoriteIconColor
                )
            }
        }
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                ErrorView(
                    message = error!!,
                    onRetry = { retry() }
                )
            }
            else -> {
                BookDetailContent(
                    book = book,
                    bookDetail = bookDetail,
                    isFavorite = isFavorite,
                    onToggleFavorite = { toggleFavorite() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


