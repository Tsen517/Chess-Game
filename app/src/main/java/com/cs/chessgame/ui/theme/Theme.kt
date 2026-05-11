package com.cs.chessgame.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = LightSquare,
    secondary = DarkSquare,
    tertiary = BoardBorder,
    background = SurfaceDark,
    surface = SurfaceDark,
    onBackground = OnSurface,
    onSurface = OnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = DarkSquare,
    secondary = LightSquare,
    tertiary = BoardBorder,
    background = OnSurface,
    surface = OnSurface,
    onBackground = SurfaceDark,
    onSurface = SurfaceDark
)

@Composable
fun ChessGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}