package com.cs.chessgame.utils

import com.cs.chessgame.model.Move
import com.cs.chessgame.model.Piece
import com.cs.chessgame.model.PieceColor
import com.cs.chessgame.model.PieceType
import kotlin.math.abs

object MoveValidator {

    /* 對外入口：幾何合法 ＋ 將軍過濾 */
    fun getValidMoves(piece: Piece, board: List<List<Piece?>>): List<Move> =
        CheckDetector.filterLegalMoves(piece, board, null)

    fun getValidMoves(piece: Piece, board: List<List<Piece?>>, lastMove: Move?): List<Move> =
        CheckDetector.filterLegalMoves(piece, board, lastMove)

    /*  純幾何移動（不含將軍過濾），供 CheckDetector 內部使用 */
    fun getRawMoves(piece: Piece, board: List<List<Piece?>>,lastMove: Move? = null): List<Move> {
        return when (piece.type) {
            PieceType.PAWN -> PawnMoves(piece, board, lastMove)
            PieceType.ROOK -> RookMoves(piece, board)
            PieceType.KNIGHT -> KnightMoves(piece, board)
            PieceType.BISHOP -> BishopMoves(piece, board)
            PieceType.QUEEN -> QueenMoves(piece, board)
            PieceType.KING -> KingMoves(piece, board)
        }
    }
    // 新增函式（ isSquareAttacked 專用）
    fun getRawMovesNoKingCastle(piece: Piece, board: List<List<Piece?>>): List<Move> {
        return when (piece.type) {
            PieceType.PAWN   -> PawnMoves(piece, board, null)
            PieceType.ROOK   -> RookMoves(piece, board)
            PieceType.KNIGHT -> KnightMoves(piece, board)
            PieceType.BISHOP -> BishopMoves(piece, board)
            PieceType.QUEEN  -> QueenMoves(piece, board)
            PieceType.KING   -> KingMovesBasic(piece, board)  // 不含易位
        }
    }

    // ──────────────────────────────────────────
    // PAWN
    // WHITE 往 row 減小（上移）；BLACK 往 row 增大（下移）
    // ──────────────────────────────────────────
    private fun PawnMoves(piece: Piece, board: List<List<Piece?>>,lastMove: Move? = null): List<Move> {
        val moves = mutableListOf<Move>()
        val dir = if (piece.color == PieceColor.WHITE) -1 else 1   // 前進方向
        val promotionRow = if (piece.color == PieceColor.WHITE) 0 else 7
        val enPassantRow = if (piece.color == PieceColor.WHITE) 3 else 4

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
                    moves += Move(
                        r, c, oneStep, nc,
                        capturedPiece = target,
                        isPromotion = oneStep == promotionRow
                    )
                }
            }
        }
        // 過路兵
        if (r == enPassantRow && lastMove != null) {
            val movedPiece = board[lastMove.toRow][lastMove.toCol]
            val isDoublePawnPush =
                movedPiece?.type == PieceType.PAWN &&
                        movedPiece.color != piece.color &&
                        abs(lastMove.toRow - lastMove.fromRow) == 2 &&
                        lastMove.toRow == r &&
                        abs(lastMove.toCol - c) == 1

            if (isDoublePawnPush) {
                moves += Move(
                    fromRow = r, fromCol = c,
                    toRow = oneStep, toCol = lastMove.toCol,
                    capturedPiece = movedPiece,
                    isEnPassant = true
                )
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
        val moves = mutableListOf<Move>()

        listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1,)
            .map { (dr, dc) -> piece.row + dr to piece.col + dc }
            .filter { (r, c) -> r in 0..7 && c in 0..7 }
            .filter { (r, c) -> board[r][c]?.color != piece.color }
            .mapTo(moves) { (r, c) ->
                Move(piece.row, piece.col, r, c, capturedPiece = board[r][c])
            }
        //王車易位：king未動且當前不在被攻擊格
        if (!piece.hasMoved &&
            !CheckDetector.isSquareAttacked(board, piece.row, piece.col, piece.color.opposite())
        ) {
            val row = piece.row

            // 短易位（King g檔，Rook h檔）
            val rookKS = board[row][7]
            if (rookKS != null && rookKS.type == PieceType.ROOK &&
                rookKS.color == piece.color && !rookKS.hasMoved &&
                board[row][5] == null && board[row][6] == null &&
                !CheckDetector.isSquareAttacked(board, row, 5, piece.color.opposite()) &&
                !CheckDetector.isSquareAttacked(board, row, 6, piece.color.opposite())
            ) {
                moves += Move(row, piece.col, row, 6, isCastling = true)
            }

            // 長易位（King c檔，Rook a檔）
            val rookQS = board[row][0]
            if (rookQS != null && rookQS.type == PieceType.ROOK &&
                rookQS.color == piece.color && !rookQS.hasMoved &&
                board[row][1] == null && board[row][2] == null && board[row][3] == null &&
                !CheckDetector.isSquareAttacked(board, row, 3, piece.color.opposite()) &&
                !CheckDetector.isSquareAttacked(board, row, 2, piece.color.opposite())
            ) {
                moves += Move(row, piece.col, row, 2, isCastling = true)
            }
        }
        return moves
    }
    private fun KingMovesBasic(piece: Piece, board: List<List<Piece?>>): List<Move> {
        return listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
            .map { (dr, dc) -> piece.row + dr to piece.col + dc }
            .filter { (r, c) -> r in 0..7 && c in 0..7 }
            .filter { (r, c) -> board[r][c]?.color != piece.color }
            .map { (r, c) -> Move(piece.row, piece.col, r, c, capturedPiece = board[r][c]) }
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