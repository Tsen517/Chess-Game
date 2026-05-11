package com.cs.chessgame.model

data class Piece(
    val type: PieceType,
    val color: PieceColor,
    val row: Int,
    val col: Int,
    val hasMoved: Boolean = false
)