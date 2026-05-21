package com.cs.chessgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cs.chessgame.ui.screen.GameScreen
import com.cs.chessgame.ui.theme.ChessGameTheme
import com.cs.chessgame.utils.SpriteSheetParser
import com.cs.chessgame.ui.theme.BoardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        SpriteSheetParser.init(this)
        setContent {
            ChessGameTheme(boardTheme = BoardTheme.CLASSIC) {
                GameScreen()
            }
        }
    }
}