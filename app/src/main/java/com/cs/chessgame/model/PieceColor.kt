package com.cs.chessgame.model

enum class PieceColor (val spriteRow: Int){
    WHITE(0), BLACK(1);
    fun opposite(): PieceColor =
        if (this == WHITE) BLACK else WHITE
}