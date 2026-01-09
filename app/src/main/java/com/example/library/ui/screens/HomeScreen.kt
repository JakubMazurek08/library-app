package com.example.library.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.library.data.model.Book
import com.example.library.data.repository.ApiResult
import com.example.library.data.repository.BookRepository
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun HomeScreen(
    repository: BookRepository,
    onBookClick: (Book) -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    // State management
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var hasMorePages by remember { mutableStateOf(true) }
    var currentOffset by remember { mutableStateOf(0) }
    var isLoadingMore by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val pageSize = 20
    val maxRandomOffset = 500
    
    // Theme-aware radial gradient with vibrant accent colors
    val isDark = isSystemInDarkTheme()
    val backgroundBrush = Brush.radialGradient(
        colors = if (isDark) {
            listOf(
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f),
                MaterialTheme.colorScheme.background
            )
        } else {
            listOf(
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f),
                MaterialTheme.colorScheme.background
            )
        },
        center = Offset(x = 600f, y = -400f),
        radius = 3000f
    )

    // Helper functions
    fun getRandomOffset(): Int {
        val maxPages = maxRandomOffset / pageSize
        return Random.nextInt(0, maxPages) * pageSize
    }
    
    fun loadBooks(refresh: Boolean = false, randomOffset: Boolean = false) {
        scope.launch {
            if (refresh) {
                isRefreshing = true
                error = null
                currentOffset = if (randomOffset) getRandomOffset() else 0
            } else {
                if (isLoadingMore) return@launch
                isLoadingMore = true
                isLoading = true
                error = null
            }
            
            try {
                when (val result = repository.getDefaultBooks(pageSize, currentOffset)) {
                    is ApiResult.Success -> {
                        val newBooks = result.data
                        books = if (refresh) newBooks else books + newBooks
                        isLoading = false
                        isRefreshing = false
                        error = null
                        hasMorePages = newBooks.size >= pageSize
                        if (!refresh) currentOffset += pageSize
                        isLoadingMore = false
                    }
                    is ApiResult.Error -> {
                        isLoading = false
                        isRefreshing = false
                        error = result.message
                        isLoadingMore = false
                    }
                    is ApiResult.Loading -> {}
                }
            } catch (e: Exception) {
                isLoading = false
                isRefreshing = false
                error = "Unexpected error: ${e.message}"
                isLoadingMore = false
            }
        }
    }
    
    fun searchBooks(query: String, randomOffset: Boolean = false) {
        scope.launch {
            searchQuery = query
            isLoading = true
            error = null
            
            if (query.isBlank()) {
                currentOffset = 0
                loadBooks(refresh = true, randomOffset = randomOffset)
                return@launch
            }
            
            try {
                currentOffset = if (randomOffset) getRandomOffset() else 0
                when (val result = repository.searchBooks(query, pageSize, currentOffset)) {
                    is ApiResult.Success -> {
                        val newBooks = result.data
                        books = newBooks
                        isLoading = false
                        error = null
                        hasMorePages = newBooks.size >= pageSize
                    }
                    is ApiResult.Error -> {
                        isLoading = false
                        error = result.message
                    }
                    is ApiResult.Loading -> {}
                }
            } catch (e: Exception) {
                isLoading = false
                error = "Unexpected error: ${e.message}"
            }
        }
    }
    
    fun loadMoreSearchResults() {
        scope.launch {
            if (isLoadingMore) return@launch
            isLoadingMore = true
            isLoading = true
            
            currentOffset += pageSize
            when (val result = repository.searchBooks(searchQuery, pageSize, currentOffset)) {
                is ApiResult.Success -> {
                    val newBooks = result.data
                    books = books + newBooks
                    isLoading = false
                    hasMorePages = newBooks.size >= pageSize
                    isLoadingMore = false
                }
                is ApiResult.Error -> {
                    isLoading = false
                    error = result.message
                    currentOffset -= pageSize
                    isLoadingMore = false
                }
                is ApiResult.Loading -> {}
            }
        }
    }
    
    // Load initial books
    LaunchedEffect(Unit) {
        loadBooks()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Book Explorer",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onNavigateToFavorites) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorites",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { query ->
                    searchBooks(query)
                },
                onClear = {
                    searchQuery = ""
                    currentOffset = 0
                    loadBooks(refresh = true)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            
            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading && books.isEmpty() -> {
                        LoadingView()
                    }
                    error != null && books.isEmpty() -> {
                        ErrorView(
                            message = error!!,
                            onRetry = { loadBooks() }
                        )
                    }
                    books.isEmpty() -> {
                        EmptyView()
                    }
                    else -> {
                        SwipeRefreshBookList(
                            books = books,
                            isRefreshing = isRefreshing,
                            onRefresh = {
                                if (searchQuery.isNotBlank()) {
                                    searchBooks(searchQuery, randomOffset = true)
                                } else {
                                    loadBooks(refresh = true, randomOffset = true)
                                }
                            },
                            onBookClick = onBookClick,
                            onLoadMore = {
                                if (!isLoading && !isLoadingMore && hasMorePages) {
                                    if (searchQuery.isBlank()) {
                                        loadBooks(refresh = false)
                                    } else {
                                        loadMoreSearchResults()
                                    }
                                }
                            },
                            isLoadingMore = isLoading
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search books...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        )
    )
}

@Composable
fun SwipeRefreshBookList(
    books: List<Book>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onBookClick: (Book) -> Unit,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean
) {
    val listState = rememberLazyListState()
    
    // Reset scroll position when refreshing to prevent crashes with new content
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            try {
                listState.scrollToItem(0)
            } catch (e: Exception) {
                // Ignore scroll errors during refresh
            }
        }
    }
    
    // Detect when we're near the end of the list for pagination
    LaunchedEffect(listState, isLoadingMore) {
        snapshotFlow { 
            try {
                val layoutInfo = listState.layoutInfo
                val totalItems = layoutInfo.totalItemsCount
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisibleItem to totalItems
            } catch (e: Exception) {
                0 to 0
            }
        }
        .collect { (lastVisibleIndex, totalItems) ->
            if (totalItems > 0 && lastVisibleIndex >= totalItems - 5 && !isLoadingMore && !isRefreshing) {
                onLoadMore()
            }
        }
    }
    
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefresh,
        swipeEnabled = !isLoadingMore && books.isNotEmpty()
    ) {
        if (books.isEmpty() && !isRefreshing) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No books available")
            }
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = books,
                    key = { book -> book.key }
                ) { book ->
                    BookListItem(
                        book = book,
                        onClick = { onBookClick(book) }
                    )
                }
                
                if (isLoadingMore && books.isNotEmpty()) {
                    item(key = "loading_indicator") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookListItem(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Book Cover
            AsyncImage(
                model = book.coverUrl,
                contentDescription = "Book cover",
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Book Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = book.authorNames,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (book.firstPublishYear != null) {
                    Text(
                        text = "First published: ${book.firstPublishYear}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Loading books...",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
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
                text = "⚠️",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}

@Composable
fun EmptyView() {
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
                text = "📚",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "No books found",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Try a different search query",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
