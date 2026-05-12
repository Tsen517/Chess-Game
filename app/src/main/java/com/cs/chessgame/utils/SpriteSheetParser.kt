package com.cs.chessgame.utils

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import com.cs.chessgame.R
import com.cs.chessgame.model.PieceColor
import com.cs.chessgame.model.PieceType

object SpriteSheetParser {

    private val pieces = mutableMapOf<Pair<PieceType, PieceColor>, ImageBitmap>()

    fun init(context: Context) {
        val res = context.resources
        val packageName = context.packageName

        PieceType.entries.forEach { type ->
            PieceColor.entries.forEach { color ->
                val resourceName = "${type.name.lowercase()}_${color.name.lowercase()}"
                val resId = res.getIdentifier(resourceName, "drawable", packageName)
                if (resId != 0) {
                    pieces[Pair(type, color)] = ImageBitmap.imageResource(res, resId)
                }
            }
        }
    }

    fun get(type: PieceType, color: PieceColor): ImageBitmap =
        pieces[Pair(type, color)] ?: error("Piece resource not found: ${type}_$color")
}
