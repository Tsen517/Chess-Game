package com.cs.chessgame.ui.theme

import androidx.compose.runtime.*

enum class BoardTheme{ CLASSIC,MINIMAL}


data class BoardColors(
    val lightSquare: androidx.compose.ui.graphics.Color,
    val darkSquare: androidx.compose.ui.graphics.Color,
    val selected: androidx.compose.ui.graphics.Color,
    val legal: androidx.compose.ui.graphics.Color,
    val check: androidx.compose.ui.graphics.Color,
    val boardBorder: androidx.compose.ui.graphics.Color,
    val surface: androidx.compose.ui.graphics.Color,
    val onSurface: androidx.compose.ui.graphics.Color
)

val ClassicBoardColors = BoardColors(
    lightSquare = ClassicLightSquare,
    darkSquare  = ClassicDarkSquare,
    selected    = ClassicSelected,
    legal       = ClassicLegal,
    check       = ClassicCheck,
    boardBorder = ClassicBoardBorder,
    surface     = ClassicSurface,
    onSurface   = ClassicOnSurface,
)

val MinimalBoardColors = BoardColors(
    lightSquare = MinimalLightSquare,
    darkSquare  = MinimalDarkSquare,
    selected    = MinimalSelected,
    legal       = MinimalLegal,
    check       = MinimalCheck,
    boardBorder = MinimalBoardBorder,
    surface     = MinimalSurface,
    onSurface   = MinimalOnSurface,
)

val LocalBoardColors = staticCompositionLocalOf { ClassicBoardColors }

@Composable
fun BoardThemeProvider(
    theme: BoardTheme = BoardTheme.CLASSIC,
    content: @Composable () -> Unit
){
    val colors = if (theme == BoardTheme.CLASSIC) ClassicBoardColors else MinimalBoardColors
    CompositionLocalProvider(LocalBoardColors provides  colors) {
        content()
    }
}
