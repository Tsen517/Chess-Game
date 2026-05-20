package com.cs.chessgame.utils

import com.cs.chessgame.model.Move
import com.cs.chessgame.model.Piece
import com.cs.chessgame.model.PieceColor
import com.cs.chessgame.model.PieceType

object MoveValidator {

    /* 對外入口：幾何合法 ＋ 將軍過濾 */
    fun getValidMoves(piece: Piece,board: List<List<Piece?>>):List<Move> =
        CheckDetector.filterLegalMoves(piece,board)

    /*  純幾何移動（不含將軍過濾），供 CheckDetector 內部使用 */
    fun getRawMoves(piece: Piece, board: List<List<Piece?>>): List<Move> {
        return when (piece.type) {
            PieceType.PAWN   -> PawnMoves(piece, board)
            PieceType.ROOK   -> RookMoves(piece, board)
            PieceType.KNIGHT -> KnightMoves(piece, board)
            PieceType.BISHOP -> BishopMoves(piece, board)
            PieceType.QUEEN -> QueenMoves(piece, board)
            PieceType.KING -> KingMoves(piece, board)
        }
    }

    // ──────────────────────────────────────────
    // PAWN
    // WHITE 往 row 減小（上移）；BLACK 往 row 增大（下移）
    // ──────────────────────────────────────────
    private fun PawnMoves(piece: Piece, board: List<List<Piece?>>): List<Move> {
        val moves = mutableListOf<Move>()
        val dir    = if (piece.color == PieceColor.WHITE) -1 else 1   // 前進方向
        val startRow = if (piece.color == PieceColor.WHITE) 6 else 1   // 初始行
        val promotionRow = if (piece.color == PieceColor.WHITE) 0 else 7

        val r = piece.row
        val c = piece.col

        // 1. 前進 1 格（目標格必須空）
        val oneStep = r + dir
        if (oneStep in 0..7 && board[oneStep][c] == null) {
            moves += Move(r, c, oneStep, c, isPromotion = oneStep == promotionRow)

            // 2. 前進 2 格（尚未移動 + 兩格都空）
            val twoStep = r + dir * 2
            if (!piece.hasMoved && twoStep in 0..7 && board[twoStep][c] == null) {
                moves += Move(r, c, twoStep, c)
            }
        }

        // 3. 斜向吃子（對角格有敵方棋子）
        for (dc in listOf(-1, 1)) {
            val nc = c + dc
            if (oneStep in 0..7 && nc in 0..7) {
                val target = board[oneStep][nc]
                if (target != null && target.color != piece.color) {
                    moves += Move(r, c, oneStep, nc,
                        capturedPiece = target,
                        isPromotion = oneStep == promotionRow)
                }
            }
        }

        return moves
    }

    // ──────────────────────────────────────────
    // ROOK
    // 四個方向持續滑動；遇己方棋子停止；遇敵方棋子吃掉後停止
    // ──────────────────────────────────────────
    private fun RookMoves(piece: Piece, board: List<List<Piece?>>): List<Move> {
    val moves = mutableListOf<Move>()
    val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1,)
        return slidingMoves(piece, board, directions)
    }
    // ─────────────────────────────────────────────
    // KNIGHT
    // ─────────────────────────────────────────────
    private fun KnightMoves(piece: Piece, board: List<List<Piece?>>): List<Move> {
        val offsets = listOf(
            -2 to -1, -2 to 1,
            -1 to -2, -1 to 2,
            1 to -2, 1 to 2,
            2 to -1, 2 to 1
        )
        return offsets
            .map { (dr, dc) -> piece.row + dr to piece.col + dc }
            .filter { (r, c) -> r in 0..7 && c in 0..7 }           // 邊界過濾
            .filter { (r, c) -> board[r][c]?.color != piece.color } // 排除己方棋子
            .map { (r, c) ->
                Move(piece.row, piece.col, r, c, capturedPiece = board[r][c])
            }
    }
    // ─────────────────────────────────────────────
    // Bishop
    // ─────────────────────────────────────────────
    private fun BishopMoves(piece: Piece, board: List<List<Piece?>>): List<Move> {
        val directions = listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)
        return slidingMoves(piece, board, directions)
    }
    // ─────────────────────────────────────────────
    // Queen（Rook 方向 + Bishop 方向）
    // ─────────────────────────────────────────────
    private fun QueenMoves(piece: Piece, board: List<List<Piece?>>): List<Move> {
        val directions = listOf(
            -1 to -1, -1 to 1, 1 to -1, 1 to 1,
            -1 to 0, 1 to 0, 0 to -1, 0 to 1
        )
        return slidingMoves(piece, board, directions)
    }
    // ─────────────────────────────────────────────
    // King（8 方向各一格）
    // ─────────────────────────────────────────────
    private fun KingMoves(piece: Piece, board: List<List<Piece?>>): List<Move> {
        val offsets = listOf(
            -1 to -1, -1 to 0, -1 to 1,
            0 to -1,          0 to 1,
            1 to -1, 1 to 0, 1 to 1,
        )
        return offsets
            .map { (dr,dc)  -> piece.row + dr to piece.col + dc }
            .filter { (r, c)   -> r in 0..7 && c in 0..7 }
            .filter { (r, c)   -> board[r][c]?.color != piece.color }
            .map    { (r, c)   ->
                Move(piece.row, piece.col, r, c, capturedPiece = board[r][c])
            }
    }
    // ── 滑動輔助（Bishop / Queen 共用，與 getRookMoves 邏輯一致）────────
    private fun slidingMoves(
        piece: Piece,
        board: List<List<Piece?>>,
        directions: List<Pair<Int, Int>>
    ): List<Move> {
        val moves = mutableListOf<Move>()
        for ((dr, dc) in directions) {
            var r = piece.row + dr
            var c = piece.col + dc
            while (r in 0..7 && c in 0..7) {
                val target = board[r][c]
                when {
                    target == null -> {
                        moves += Move(piece.row, piece.col, r, c)
                    }
                    target.color != piece.color -> {
                        moves += Move(piece.row, piece.col, r, c, capturedPiece = target)
                        break
                    }
                    else -> break  // 友軍，停止
                }
                r += dr; c += dc
            }
        }
        return moves
    }

}