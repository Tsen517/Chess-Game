package com.cs.chessgame.utils

import com.cs.chessgame.model.Move
import com.cs.chessgame.model.Piece
import com.cs.chessgame.model.PieceColor
import com.cs.chessgame.model.PieceType
import com.cs.chessgame.utils.CheckDetector.filterLegalMoves
import com.cs.chessgame.utils.CheckDetector.isInCheck



object CheckDetector {
    /* 模擬移動後，回傳新棋盤（不改動原始state）*/
    fun applyMoveBoard(
        board: List<List<Piece?>>,
        move: Move
    ): List<List<Piece?>> {
        val b = board.map { it.toMutableList() }.toMutableList()
        val piece = b[move.fromRow][move.fromCol]!!
        b[move.fromRow][move.fromCol] = null
        b[move.toRow][move.toCol] = piece.copy(
            row = move.toRow,col = move.toCol,hasMoved = true
        )
        //王車易位
        if (move.isCastling){
            val row = move.fromRow
            if (move.toCol == 6){ //短易位
                val rook = b[row][7]!!
                b[row][7] = null
                b[row][5] = rook.copy(col=5, hasMoved = true)
            }else if (move.toCol == 2){
                val rook = b[row][0]!!
                b[row][0] = null
                b[row][3] = rook.copy(col = 3, hasMoved = true)
            }
        }
        // 過路兵：移除被吃的兵（在同行不同格）
        if (move.isEnPassant) {
            b[move.fromRow][move.toCol] = null
        }
        return b.map { it.toList() }
    }

    /* 指定格子是否被attackerColor 的任一棋子攻擊 */
    fun isSquareAttacked(
        board: List<List<Piece?>>,
        row: Int,
        col: Int,
        attackerColor: PieceColor
    ): Boolean {
        for (r in 0..7) for (c in 0..7){
            val piece = board[r][c] ?: continue
            if (piece.color != attackerColor) continue
            if (MoveValidator.getRawMovesNoKingCastle(piece,board)
                    .any{it.toRow == row && it.toCol == col }) return true
        }
        return false
    }
    /* 指定顏色的 King 事否在被將軍 */
    fun isInCheck(board: List<List<Piece?>>, color: PieceColor): Boolean{
        val king = board.flatten().filterNotNull()
            .first{it.type == PieceType.KING && it.color == color}
        return isSquareAttacked(board,king.row,king.col,color.opposite())
    }

    /* 執行 moved 後，己方 King 是否仍被將軍（非法移動） */
    fun isMoveLegal(board: List<List<Piece?>>,move: Move,color:PieceColor): Boolean{
        val newBoard = applyMoveBoard(board,move)
        return !isInCheck(newBoard,color)
    }

    /* 過濾掉會讓己方暴露 King 的舉動 */
    fun filterLegalMoves(
        piece: Piece,
        board: List<List<Piece?>>,
        lastMove: Move? = null
    ): List<Move> =MoveValidator.getRawMoves(piece,board,lastMove)
            .filter { isMoveLegal(board,it,piece.color) }

    fun isCheckmate(board: List<List<Piece?>>, color: PieceColor,lastMove: Move? = null): Boolean {
        if (!isInCheck(board, color)) return false
        return board.flatten().filterNotNull()
            .filter { it.color == color }
            .all { filterLegalMoves(it, board,lastMove).isEmpty() }
    }
    fun isStalemate(board: List<List<Piece?>>,color: PieceColor,lastMove: Move? = null): Boolean{
        if(isInCheck(board,color))return false
        return board.flatten()
            .filterNotNull()
            .filter { it.color == color }
            .flatMap { filterLegalMoves(it,board,lastMove) }
            .isEmpty()
    }
}
