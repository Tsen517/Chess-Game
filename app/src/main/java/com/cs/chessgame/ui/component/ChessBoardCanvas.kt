package com.cs.chessgame.ui.component

import android.R
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.cs.chessgame.model.GameState
import com.cs.chessgame.model.Move
import com.cs.chessgame.ui.theme.BoardColors
import com.cs.chessgame.utils.SpriteSheetParser
import com.cs.chessgame.viewmodel.GameViewModel
import com.cs.chessgame.ui.theme.LocalBoardColors

@Composable
fun ChessBoardCanvas(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val selectedSquare by viewModel.selectedSquare.collectAsState()
    val validMoves by viewModel.validMoves.collectAsState()
    val boardColors = LocalBoardColors.current

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(12.dp)
            //點擊偵測：像素座標轉換成格子
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val cellSize = size.width / 8f
                    val col = (offset.x / cellSize).toInt().coerceIn(0, 7)
                    val row = (offset.y / cellSize).toInt().coerceIn(0, 7)
                    viewModel.onSquareClick(row, col)
                }
            }
    ) {
        val cellSize = size.width / 8f


        // 1. 繪製棋盤底色
        drawBoard(cellSize,boardColors)

        // 2. 將軍紅框
        if (gameState.isCheck){
            val king = gameState.board.flatten().filterNotNull()
                .first{ it.type == com.cs.chessgame.model.PieceType.KING && it.color == gameState.currentTurn }
            drawRect(
                color = boardColors.check,
                topLeft = Offset(king.col * cellSize,king.row*cellSize),
                size = Size(cellSize,cellSize)
            )
        }
        // 3. 選取框（黃色，畫在棋子之下以免蓋住棋子邊緣）
        selectedSquare?.let { (selRow, selCol) ->
            drawSelectedSquare(selRow, selCol, cellSize,boardColors)
        }

        // 4. 合法移動提示點（綠色半透明圓）
        drawValidMoveHints(validMoves, gameState, cellSize,boardColors)

        // 5. 繪製所有棋子(最上層)
        drawAllPieces(gameState, cellSize)
    }
}

// ─────────────────────────────────────────────────────────
// 選取框：黃色實心背景 + 深色邊框，不遮棋子
// ─────────────────────────────────────────────────────────
private fun DrawScope.drawSelectedSquare(row: Int, col: Int, cellSize: Float,boardColors: BoardColors) {
    val topLeft = Offset(col*cellSize, row*cellSize)
    val size = Size(cellSize,cellSize)

    //半透明黃色填滿以示選取區塊
    drawRect(
        color = boardColors.selected,
        topLeft = topLeft,
        size = size
    )
    //黑色邊框
    drawRect(
        color = boardColors.selected,
        topLeft = topLeft,
        size = size,
        style = Stroke(width = 3.dp.toPx())
    )
}

// ─────────────────────────────────────────────────────────
// 合法移動提示：
//   空格 → 小綠點（置中）
//   有敵方棋子 → 綠色空心環（表示可吃）
// ─────────────────────────────────────────────────────────
private  fun DrawScope.drawValidMoveHints(
    moves:List<Move>,
    gameState: GameState,
    cellSize: Float,
    boardColors: BoardColors
){
    moves.forEach { move ->
        val cx = move.toCol * cellSize + cellSize / 2f
        val cy = move.toRow * cellSize + cellSize / 2f
        val hasEnemy = gameState.board[move.toRow][move.toCol] != null

        if (hasEnemy){
            //空心環，表示能吃子
            drawCircle(
                color = boardColors.legal,
                radius = cellSize *0.46f,
                center = Offset(cx,cy),
                style = Stroke(width = 4.dp.toPx())
            )
        }else{
            // 半透明綠色填滿整格，表示可移動
            drawRect(
                color = boardColors.legal,
                topLeft = Offset(x = move.toCol * cellSize, y = move.toRow * cellSize),
                size = Size(cellSize, cellSize)
            )
        }
    }
}

private fun DrawScope.drawBoard(cellSize: Float,boardColors: BoardColors) {
    val light = Color(0xFFF0D9B5)
    val dark = Color(0xFFB58863)
    for (row in 0..7) {
        for (col in 0..7) {
            drawRect(
                color = if ((row + col) % 2 == 0) boardColors.lightSquare  else boardColors.darkSquare,
                topLeft = Offset(col * cellSize, row * cellSize),
                size = Size(cellSize, cellSize)
            )
        }
    }
    // 新增：棋盤外框
    drawRect(
        color = boardColors.boardBorder,
        topLeft = Offset(0f, 0f),
        size = Size(size.width, size.height),
        style = Stroke(width = 3.dp.toPx())
    )
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