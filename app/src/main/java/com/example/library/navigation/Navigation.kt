package com.example.library.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.library.data.model.Book
import com.example.library.ui.screens.BookDetailScreen
import com.example.library.ui.screens.FavoritesScreen
import com.example.library.ui.screens.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object BookDetail : Screen("book_detail/{workId}/{title}/{authors}/{coverId}/{year}") {
        fun createRoute(book: Book): String {
            val workId = Uri.encode(book.workId)
            val title = Uri.encode(book.title)
            val authors = Uri.encode(book.authorNames)
            val coverId = book.coverId?.toString() ?: "0"
            val year = book.firstPublishYear?.toString() ?: "0"
            return "book_detail/$workId/$title/$authors/$coverId/$year"
        }
    }
    object Favorites : Screen("favorites")
}

@Composable
fun BookExplorerNavigation(
    repository: com.example.library.data.repository.BookRepository,
    favoritesManager: com.example.library.data.preferences.FavoritesManager,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                repository = repository,
                onBookClick = { book ->
                    val route = Screen.BookDetail.createRoute(book)
                    navController.navigate(route)
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }
        
        // Book Detail Screen
        composable(
            route = Screen.BookDetail.route,
            arguments = listOf(
                navArgument("workId") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("authors") { type = NavType.StringType },
                navArgument("coverId") { type = NavType.StringType },
                navArgument("year") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val workId = Uri.decode(backStackEntry.arguments?.getString("workId") ?: "")
            val title = Uri.decode(backStackEntry.arguments?.getString("title") ?: "")
            val authorsString = Uri.decode(backStackEntry.arguments?.getString("authors") ?: "")
            val coverIdStr = backStackEntry.arguments?.getString("coverId") ?: "0"
            val yearStr = backStackEntry.arguments?.getString("year") ?: "0"
            
            val coverId = coverIdStr.toIntOrNull()?.takeIf { it != 0 }
            val year = yearStr.toIntOrNull()?.takeIf { it != 0 }
            
            // Recreate Book object from parameters
            val book = Book(
                key = "/works/$workId",
                title = title,
                authors = if (authorsString.isNotBlank() && authorsString != "Unknown Author") {
                    authorsString.split(", ").map { com.example.library.data.model.Author(name = it) }
                } else null,
                coverId = coverId,
                firstPublishYear = year,
                editionCount = null
            )
            
            BookDetailScreen(
                book = book,
                repository = repository,
                favoritesManager = favoritesManager,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Favorites Screen
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                favoritesManager = favoritesManager,
                onBookClick = { book ->
                    val route = Screen.BookDetail.createRoute(book)
                    navController.navigate(route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

