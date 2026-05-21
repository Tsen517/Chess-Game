package com.cs.chessgame.ui.component



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs.chessgame.model.PieceColor
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text


@Composable
fun GameOverlay(
    isCheckmate: Boolean,
    isStalemate: Boolean,
    isTimeout: Boolean,
    currentTurn: PieceColor, // applyMove 後已換手：此為敗方
    onRestart: () -> Unit
){
    if (!isCheckmate && !isStalemate && !isTimeout) return

    val loser = currentTurn
    val winner = currentTurn.opposite()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF727294)),
            elevation = CardDefaults.cardElevation(12.dp),
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            Column(
                modifier = Modifier.padding(36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = when {
                        isCheckmate -> "♔"
                        isTimeout   -> "⏰"
                        else        -> "🤝"
                    },
                    fontSize = 56.sp
                )
                Text(
                    text = when {
                        isCheckmate -> "將死"
                        isTimeout   -> "超時"
                        else        -> "和局"
                    },
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = when {
                        isCheckmate -> "${if (winner == PieceColor.WHITE) "白方" else "黑方"} 獲勝"
                        isTimeout   -> "${if (loser == PieceColor.WHITE) "白方" else "黑方"} 超時，${if (winner == PieceColor.WHITE) "白方" else "黑方"} 獲勝"
                        else        -> "雙方平手"
                    },
                    fontSize = 18.sp,
                    color = Color(0xFFB0B0C0)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onRestart,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ){
                    Text(
                        text = "重新開始",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}