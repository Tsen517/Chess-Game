package com.cs.chessgame.viewmodel

import androidx.lifecycle.ViewModel
import com.cs.chessgame.model.GameState
import com.cs.chessgame.model.Piece
import com.cs.chessgame.model.PieceColor
import com.cs.chessgame.model.PieceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(GameState(initialBoard()))
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
}

fun initialBoard(): List<List<Piece?>>{
    val board = Array(8){arrayOfNulls<Piece>(8)}

    val backRow = listOf(
        PieceType.ROOK,
        PieceType.KNIGHT,
        PieceType.BISHOP,
        PieceType.QUEEN,
        PieceType.KING,
        PieceType.BISHOP,
        PieceType.KNIGHT,
        PieceType.ROOK
    )
    backRow.forEachIndexed { col, type ->
        board[0][col] = Piece(type, PieceColor.BLACK, 0, col)  // 黑方上方
        board[7][col] = Piece(type, PieceColor.WHITE, 7, col)  // 白方下方
    }
    for (col in 0..7) {
        board[1][col] = Piece(PieceType.PAWN, PieceColor.BLACK, 1, col)  // 黑兵
        board[6][col] = Piece(PieceType.PAWN, PieceColor.WHITE, 6, col)  // 白兵
    }
    return board.map { it.toList() }
}