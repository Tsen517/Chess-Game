package com.cs.chessgame.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.cs.chessgame.model.GameState
import com.cs.chessgame.utils.SpriteSheetParser
import com.cs.chessgame.viewmodel.GameViewModel

@Composable
fun ChessBoardCanvas(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(12.dp)
    ) {
        val cellSize = size.width / 8f

        // 1. 繪製棋盤底色
        drawBoard(cellSize)

        // 2. 繪製所有棋子
        drawAllPieces(gameState, cellSize)
    }
}

private fun DrawScope.drawBoard(cellSize: Float) {
    val light = Color(0xFFF0D9B5)
    val dark = Color(0xFFB58863)
    for (row in 0..7) {
        for (col in 0..7) {
            drawRect(
                color = if ((row + col) % 2 == 0) light else dark,
                topLeft = Offset(col * cellSize, row * cellSize),
                size = Size(cellSize, cellSize)
            )
        }
    }
}

private fun DrawScope.drawAllPieces(gameState: GameState, cellSize: Float) {
    for (row in 0..7) {
        for (col in 0..7) {
            val piece = gameState.board[row][col] ?: continue
            // 獲取預處理好的獨立棋子圖片 (已去底、已裁切、已染色)
            val bitmap = SpriteSheetParser.get(piece.type, piece.color)
            drawPieceInCell(row, col, bitmap, cellSize)
        }
    }
}

private fun DrawScope.drawPieceInCell(
    row: Int,
    col: Int,
    bitmap: ImageBitmap,
    cellSize: Float
) {
    // 1. 決定棋子在棋格內的佔比 (例如高度佔棋格的 80%)
    val pieceScaleFactor = 0.8f
    val maxPieceHeight = cellSize * pieceScaleFactor

    // 2. 保持原始比例計算顯示尺寸
    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
    val dstHeight = maxPieceHeight
    val dstWidth = maxPieceHeight * aspectRatio

    // 如果棋子太寬（雖然棋子通常是高的，但為了保險），則以寬度為基準縮放
    val finalWidth = if (dstWidth > cellSize * pieceScaleFactor) cellSize * pieceScaleFactor else dstWidth
    val finalHeight = finalWidth / aspectRatio

    // 3. 計算座標以實現「完全置中」
    // (col * cellSize) 是棋格左邊緣，加上 (剩餘空間 / 2) 達成置中
    val left = col * cellSize + (cellSize - finalWidth) / 2f
    val top = row * cellSize + (cellSize - finalHeight) / 2f

    val dstSize = IntSize(finalWidth.toInt(), finalHeight.toInt())
    val dstOffset = IntOffset(left.toInt(), top.toInt())

    // 4. 繪製柔和陰影 (向右下方稍微偏移，模擬光源)
    val shadowOffset = IntOffset(
        (left + cellSize * 0.05f).toInt(),
        (top + cellSize * 0.05f).toInt()
    )
    drawImage(
        image = bitmap,
        dstOffset = shadowOffset,
        dstSize = dstSize,
        colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.15f), BlendMode.SrcIn)
    )

    // 5. 繪製棋子本體
    drawImage(
        image = bitmap,
        dstOffset = dstOffset,
        dstSize = dstSize
    )
}