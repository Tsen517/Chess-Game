package com.cs.chessgame.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ClassicColorScheme = darkColorScheme(
    primary      = PrimaryAccent,
    onPrimary    = OnPrimary,
    background   = AppBackground,
    surface      = ClassicSurface,
    onBackground = ClassicOnSurface,
    onSurface    = ClassicOnSurface,
)

private val MinimalColorScheme = darkColorScheme(
    primary      = PrimaryAccent,
    onPrimary    = OnPrimary,
    background   = AppBackground,
    surface      = MinimalSurface,
    onBackground = MinimalOnSurface,
    onSurface    = MinimalOnSurface,
)

@Composable
fun ChessGameTheme(
    boardTheme : BoardTheme = BoardTheme.CLASSIC,
    content    : @Composable () -> Unit
) {
    val colorScheme = if (boardTheme == BoardTheme.CLASSIC) ClassicColorScheme else MinimalColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
    ) {
        BoardThemeProvider(theme = boardTheme) {
            content()
        }
    }
}