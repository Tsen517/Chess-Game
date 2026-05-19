package com.cs.chessgame.viewmodel

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs.chessgame.model.GameState
import com.cs.chessgame.model.Move
import com.cs.chessgame.model.Piece
import com.cs.chessgame.model.PieceColor
import com.cs.chessgame.model.PieceType
import com.cs.chessgame.utils.CheckDetector
import com.cs.chessgame.utils.MoveValidator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(GameState(initialBoard()))
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    /* 目前選中格子，null代表未選取*/
    private val _selectedSquare = MutableStateFlow<Pair<Int, Int>?>(null)
    val selectedSquare: StateFlow<Pair<Int, Int>?> = _selectedSquare.asStateFlow()

    /*目前合法移動清單（給UI畫提示點）*/
    private val _vaildMoves = MutableStateFlow<List<Move>>(emptyList())
    val validMoves: StateFlow<List<Move>> = _vaildMoves.asStateFlow()

    // 新增計時器
    private var timerJob: Job?=null
    init {
        startTimer()
    }
    //計時器：每秒倒數目前回合的玩家
    private fun startTimer(){
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true){
                delay(1000L)
                val state = _gameState.value
                if (state.isCheckmate || state.isStalemate) break

                val isWhiteTurn = state.currentTurn == PieceColor.WHITE
                val newWhite = if(isWhiteTurn)(state.whiteTimeSeconds -1).coerceAtLeast(0)
                               else state.whiteTimeSeconds
                val newBlack = if(!isWhiteTurn)(state.blackTimeSeconds -1).coerceAtLeast(0)
                               else state.blackTimeSeconds

                _gameState.value = state.copy(
                    whiteTimeSeconds = newWhite,
                    blackTimeSeconds = newBlack
                )
            }
        }
    }

    // 點擊格子邏輯 //
    fun onSquareClick(row: Int, col: Int){
        val state = _gameState.value
        if (state.isCheckmate || state.isStalemate) return
        val selected = _selectedSquare.value
        if (selected == null){
            selectPiece(row, col,state)
        }else{
            val(selRow,selCol) = selected

            when{
                //點同一格 -> 則取消選取
                selRow === row && selCol ===  col -> clearSelection()
                //點合法落點 -> 執行移動
                _vaildMoves.value.any{it.toRow == row && it.toCol == col} -> {
                    val move = _vaildMoves.value.first{it.toRow == row && it.toCol == col}
                    applyMove(move,state)
                }
                //點己方其他棋子 ->改選則其他棋子
                else -> selectPiece(row, col,state)
            }
        }
    }
    // ─────────────────────────────────────────────
    // 選取棋子
    // ─────────────────────────────────────────────
    private fun selectPiece(row: Int,col: Int,state: GameState){
        val piece = state.board[row][col]
        if (piece == null || piece.color != state.currentTurn){
            clearSelection()
            return
        }
        _selectedSquare.value = row to col
        _vaildMoves.value = MoveValidator.getValidMoves(piece,state.board)

    }
    // ─────────────────────────────────────────────
    // 執行移動，更新 board + 換手
    // ─────────────────────────────────────────────
    private fun applyMove(move: Move,state: GameState){
        val board = state.board.map { it.toMutableStateList() }.toMutableStateList()
        val movingPiece = board[move.fromRow][move.fromCol]!!

        val updatedPiece = movingPiece.copy(
            row = move.toRow,
            col = move.toCol,
            hasMoved = true
        )
        board[move.fromRow][move.fromCol] = null
        board[move.toRow][move.toCol] = updatedPiece

        val nextTurn = state.currentTurn.opposite()
        val newBoard = board.map{ it.toList() }
        val inCheck = CheckDetector.isInCheck(newBoard,nextTurn)
        val inCheckmate = CheckDetector.isCheckmate(newBoard,nextTurn)
        val isStalemate = CheckDetector.isStalemate(newBoard,nextTurn)

        _gameState.value = state.copy(
            board = newBoard,
            currentTurn = nextTurn,
            moveHistory = state.moveHistory + move,
            isCheck = inCheck,
            isCheckmate = inCheckmate,
            isStalemate = isStalemate
        )
        clearSelection()
    }
    // ─────────────────────────────────────────────
    // 悔棋：還原上一步
    // ─────────────────────────────────────────────
    fun undoMove(){
        val state = _gameState.value
        if (state.moveHistory.isEmpty()) return

        val lastMove = state.moveHistory.last()
        val board = state.board.map { it.toMutableStateList() } .toMutableStateList()

        val movedPiece = board[lastMove.toRow][lastMove.toCol]!!
        board[lastMove.toRow][lastMove.toCol] = lastMove.capturedPiece
        board[lastMove.fromRow][lastMove.fromCol] = movedPiece.copy(
            row = lastMove.fromRow,
            col = lastMove.fromCol,
            hasMoved = lastMove.fromRow != (if (movedPiece.color == PieceColor.WHITE)6 else 1)
        )

        val newHistory = state.moveHistory.dropLast(1)
        val newBoard = board.map{it.toList()}
        val prevTurn = state.currentTurn.opposite()
        val inCheck = CheckDetector.isInCheck(newBoard,prevTurn)

        _gameState.value = state.copy(
            board = newBoard,
            currentTurn = prevTurn,
            moveHistory = newHistory,
            isCheck = inCheck,
            isCheckmate = false,
            isStalemate = false
        )
        clearSelection()
    }
    // ─────────────────────────────────────────────
    // 重新開始
    // ─────────────────────────────────────────────
    fun resetGame(){
        _gameState.value = GameState(board= initialBoard())
        clearSelection()
        startTimer()
    }
    // ─────────────────────────────────────────────
    // 清除選取狀態
    // ─────────────────────────────────────────────
    private fun clearSelection(){
        _selectedSquare.value = null
        _vaildMoves.value = emptyList()
    }
}
// ─────────────────────────────────────────────
// 初始棋盤
// ─────────────────────────────────────────────
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
// ─────────────────────────────────────────────
// 時間格式化工具
// ─────────────────────────────────────────────
fun formatTime(seconds:Int): String{
    val m = seconds / 60
    val s = seconds % 60
    return "%02d%02d".format(m,s)
}