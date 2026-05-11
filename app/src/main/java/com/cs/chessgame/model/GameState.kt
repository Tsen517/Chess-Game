package com.cs.chessgame.model

data class GameState(
    val board: List<List<Piece?>> = emptyBoard(),
    val currentTurn: PieceColor = PieceColor.WHITE,
    val moveHistory: List<Move> = emptyList(),
    val isCheck: Boolean = false,
    val isCheckmate: Boolean = false
)

fun emptyBoard(): List<List<Piece?>> = List(8) { List(8) { null } }