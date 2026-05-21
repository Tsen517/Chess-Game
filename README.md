# ♟ Android Chess Game

Kotlin + Jetpack Compose · 雙人本地對戰 · 3 週衝刺專題

---

## 專案簡介

以 Android MVVM 架構實作的雙人本地對戰西洋棋遊戲。
透過完整棋類遊戲練習 ViewModel / StateFlow 分層設計，以及 Compose Canvas 自訂繪圖與觸控事件處理。

---

## 技術架構

| 項目 | 內容 |
|------|------|
| 語言 | Kotlin |
| UI 框架 | Jetpack Compose |
| 架構模式 | MVVM |
| 狀態管理 | StateFlow |
| 繪圖 | Compose Canvas（DrawScope） |
| 觸控 | Modifier.pointerInput + detectTapGestures |
| 計時器 | viewModelScope + coroutine delay |

---

## 功能進度

### ✅ 已完成

- 棋盤渲染（8×8 Canvas 繪製）
- 棋子圖示（PNG Sprite Sheet 載入與裁切）
- 六種棋子合法走法（Pawn / Rook / Knight / Bishop / Queen / King）
- 雙人本地對戰（回合制換手）
- 將軍偵測與紅色高亮
- 將死 / 和局偵測
- 遊戲結束 Overlay 畫面
- 悔棋（Undo）
- 計時器倒數 UI（每方 10 分鐘）
- 兵升變（Pawn Promotion）
- 王車易位（Castling）
- 過路兵（En Passant）
- 超時判負

### 🔲 待完成


- 主選單畫面
- 音效
- 棋譜顯示
- 單元測試

---

## 專案結構

```
com.cs.chessgame
├── model/
│   ├── GameState.kt
│   ├── Move.kt
│   ├── Piece.kt
│   ├── PieceColor.kt
│   └── PieceType.kt
├── ui/
│   ├── component/
│   │   ├── ChessBoardCanvas.kt
│   │   ├── GameOverlay.kt
│   │   └── PromotionDialog.kt
│   ├── screen/
│   │   └── GameScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── utils/
│   ├── CheckDetector.kt
│   ├── MoveValidator.kt
│   └── SpriteSheetParser.kt
├── viewmodel/
│   └── GameViewModel.kt
└── MainActivity.kt
```

---

## 成員分工

| 成員 | 負責項目 |
|------|---------|
| A（邏輯工程師） | MoveValidator、GameState、悔棋、計時器邏輯 |
| B（UI 工程師） | ChessBoardCanvas、動畫、主題設計、音效整合 |
| 共同 | Git flow、週末整合測試、期末簡報 |

---

## Credits

## Credits

### Chess Piece Images

Chess pieces image © [Stilfehler](https://commons.wikimedia.org/wiki/User:Stilfehler),
licensed under [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/),
via [Wikimedia Commons](https://commons.wikimedia.org/wiki/File:Chess_Pieces_20.png)
> 原始圖片經裁切為 12 張獨立棋子圖片使用於遊戲中，
> 依 CC BY-SA 4.0 規定本專題以相同授權釋出。
