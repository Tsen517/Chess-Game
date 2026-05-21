package com.cs.chessgame.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs.chessgame.model.PieceColor
import com.cs.chessgame.ui.component.ChessBoardCanvas
import com.cs.chessgame.ui.component.GameOverlay
import com.cs.chessgame.viewmodel.GameViewModel
import com.cs.chessgame.viewmodel.formatTime
import com.cs.chessgame.ui.component.PromotionDialog


@Composable
fun GameScreen(viewModel: GameViewModel = viewModel()) {
    val gameState by viewModel.gameState.collectAsState()
    val pendingPromotion by viewModel.pendingPromotion.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(Color(0xFF1E1E2E)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 黑方計時器（上方）
        TimerRow(
            label = "黑方",
            timeSeconds = gameState.blackTimeSeconds,
            isActive = gameState.currentTurn == PieceColor.BLACK &&
                    !gameState.isCheckmate && !gameState.isStalemate
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 棋盤 + Overlay
        Box(modifier = Modifier.fillMaxWidth()) {
            ChessBoardCanvas(
                viewModel = viewModel,
                modifier = Modifier.fillMaxWidth()
            )
            GameOverlay(
                isCheckmate = gameState.isCheckmate,
                isStalemate = gameState.isStalemate,
                isTimeout = gameState.isTimeout,
                currentTurn = gameState.currentTurn,
                onRestart = { viewModel.resetGame() }
            )
            pendingPromotion?.let { (row, _) ->
                PromotionDialog(
                    color = gameState.currentTurn,
                    onPieceSelected = { viewModel.onPromotionSelected(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 白方計時器（下方）
        TimerRow(
            label = "白方",
            timeSeconds = gameState.whiteTimeSeconds,
            isActive = gameState.currentTurn == PieceColor.WHITE &&
                    !gameState.isCheckmate && !gameState.isStalemate
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 悔棋按鈕
        Button(
            onClick = { viewModel.undoMove() },
            enabled = gameState.moveHistory.isNotEmpty() &&
                    !gameState.isCheckmate && !gameState.isStalemate,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3A3A5C),
                disabledContainerColor = Color(0xFF2A2A3C)
            ),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp)
        ) {
            Text(
                text = "↩ 悔棋",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun TimerRow(
    label:String,
    timeSeconds:Int,
    isActive: Boolean
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isActive) Color(0XFF3A3A5C) else Color(0xFF2A2A3C)
            )
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = if (isActive)Color.White else Color(0xFF8888AA)
        )
        Text(
            text = formatTime(timeSeconds),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = when{
                timeSeconds <= 30 -> Color(0xFFFF5252)
                isActive          -> Color.White
                else              -> Color(0xFF8888AA)
            }
        )
    }
}