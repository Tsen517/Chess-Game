package com.cs.chessgame.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs.chessgame.model.PieceColor
import com.cs.chessgame.model.PieceType
import com.cs.chessgame.utils.SpriteSheetParser

@Composable
fun PromotionDialog(
    color: PieceColor,
    onPieceSelected: (PieceType) -> Unit
) {
    val options = listOf(PieceType.QUEEN, PieceType.ROOK,
        PieceType.BISHOP, PieceType.KNIGHT)
    val label = if (color == PieceColor.WHITE) "White" else "Black"

    AlertDialog(
        onDismissRequest = { /* 強制選擇，不允許關閉 */ },
        confirmButton = { },
        title = { Text("$label Pawn Promotion") },
        text = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { type ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onPieceSelected(type) }
                            .padding(8.dp)
                    ) {
                        // 用現有 SpriteSheetParser 取得 bitmap
                            Image(
                                bitmap = SpriteSheetParser.get(type, color),
                                contentDescription = type.name,
                                modifier = Modifier.size(56.dp)
                            )
                        Text(
                            text = type.name.lowercase()
                                .replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    )
}