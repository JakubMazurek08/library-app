package com.example.library.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BookBlueLight,
    onPrimary = Color(0xFF003258),
    primaryContainer = BookBluePale,
    onPrimaryContainer = Color(0xFF001D36),
    
    secondary = BookTealPale,
    onSecondary = Color(0xFF003731),
    secondaryContainer = BookTealVeryLight,
    onSecondaryContainer = Color(0xFF002019),
    
    tertiary = BookAmberLight,
    onTertiary = Color(0xFF402D00),
    tertiaryContainer = Color(0xFFFFE082),
    onTertiaryContainer = Color(0xFF2E2000),
    
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF44474F),
    onSurfaceVariant = TextSecondaryDark,
    
    outline = Color(0xFF8E9099),
    outlineVariant = Color(0xFF44474F)
)

private val LightColorScheme = lightColorScheme(
    primary = BookBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = BookBlueDark,
    
    secondary = BookTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF004D40),
    
    tertiary = BookAmber,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFECB3),
    onTertiaryContainer = Color(0xFF3E2723),
    
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = TextSecondary,
    
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0)
)

@Composable
fun LibraryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled by default for consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}