package com.cs.chessgame.model

data class Move(
    val fromRow: Int,
    val fromCol: Int,
    val toRow: Int,
    val toCol: Int,
    val capturedPiece: Piece? = null,
    val isPromotion: Boolean = false,
    val isCastling: Boolean = false,
    val isEnPassant: Boolean = false
)