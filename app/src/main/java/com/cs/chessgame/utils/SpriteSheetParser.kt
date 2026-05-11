package com.cs.chessgame.utils

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.Bitmap
import com.cs.chessgame.R
import com.cs.chessgame.model.PieceColor
import com.cs.chessgame.model.PieceType

object SpriteSheetParser {
    private lateinit var pieces: Array<Array<ImageBitmap>>

    fun init(context: Context) {
        val src = BitmapFactory.decodeResource(
            context.resources, R.drawable.chess_pieces
        )
        val w = src.width / 6
        val h = src.height / 2

        pieces = Array(2) { row ->
            Array(6) { col ->
                Bitmap.createBitmap(src, col * w, row * h, w, h)
                    .asImageBitmap()
            }
        }
    }

    fun get(type: PieceType, color: PieceColor): ImageBitmap =
        pieces[color.spriteRow][type.spriteCol]
}