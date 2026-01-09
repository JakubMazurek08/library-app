# Book Explorer

A modern Android application built with Jetpack Compose that allows users to search for books, view detailed information, and manage their favorite collection.

## Features

*   **Book Search**: Search for books using the Open Library API.
*   **Book Details**: View detailed information about a book, including its description, authors, and publication year.
*   **Favorites**: Save books to a local favorites list for quick access.
*   **Smooth Navigation**: Uses Jetpack Compose Navigation with debounced events to prevent UI crashes during transitions.
*   **Responsive UI**: Built entirely with Jetpack Compose for a modern and fluid user experience.

## Tech Stack

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   **Navigation**: [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)
*   **Networking**: [Retrofit](https://square.github.io/retrofit/) with GSON converter
*   **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
*   **Local Storage**: [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) for managing favorites.
*   **Lifecycle**: [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) and Lifecycle-aware components.

## Architecture

The project follows modern Android development practices:
*   **Repository Pattern**: Centralized data access from network and local storage.
*   **Clean Navigation**: Decoupled navigation logic using a sealed class for routes.

## Getting Started

1.  Clone the repository.
2.  Open the project in Android Studio.
3.  Sync Gradle and run the app on an emulator or physical device.

## License

This project is for educational purposes. Book data is provided by the [Open Library API](https://openlibrary.org/developers/api).
