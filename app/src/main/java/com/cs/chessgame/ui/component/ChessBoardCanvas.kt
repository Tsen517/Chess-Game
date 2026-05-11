package com.cs.chessgame.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.cs.chessgame.model.GameState
import com.cs.chessgame.ui.theme.DarkSquare
import com.cs.chessgame.ui.theme.LightSquare

@Composable
fun ChessBoardCanvas(
    gameState: GameState,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.aspectRatio(1f))  {
        val cellSize = size.width / 8f

        //繪製棋盤
        for (row in 0..7){
            for (col in 0..7){
                val color = if ((row + col) % 2 == 0) LightSquare else DarkSquare
                drawRect(
                    color = color,
                    topLeft = Offset(col * cellSize,row * cellSize),
                    size = Size(cellSize,cellSize)
                )
            }
        }
    }
}