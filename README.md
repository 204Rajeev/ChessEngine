# Java Chess Engine

A fully functional chess engine built from scratch in Java using Spring Boot, featuring a minimax search algorithm with alpha-beta pruning and a multi-term evaluation function. The engine plays as Black against a human player via a web-based frontend using chessboard.js.

---

## Features

### Game Logic
- Complete move generation for all pieces — Pawn, Knight, Bishop, Rook, Queen, King
- En passant with correct one-move expiry window
- Castling (kingside and queenside) with all legality checks — king not in check, not passing through check, pieces not moved
- Pawn promotion — auto-promotes to Queen (correct engine behavior)
- Check and checkmate detection
- Stalemate detection

### Search Algorithm
- Minimax with alpha-beta pruning
- Make/unmake move optimization — board is mutated in place and restored, eliminating unnecessary deep copies for illegal moves
- Move ordering using MVV-LVA (Most Valuable Victim, Least Valuable Attacker) — captures searched first for better pruning
- Transposition table with EXACT / LOWER_BOUND / UPPER_BOUND flags
- Configurable search depth (currently depth 4)

### Evaluation Function
All terms computed in a single board pass for efficiency:

| Term | Description |
|---|---|
| Material | Centipawn values — Pawn=100, Knight=310, Bishop=300, Rook=500, Queen=900 |
| Piece-Square Tables | Positional bonuses/penalties for all pieces (Pawn, Knight, Bishop, Rook, Queen) |
| Passed Pawns | Rank-scaled bonus for pawns with no enemy blockers ahead |
| Rook on Open File | +25 for fully open file, +15 for semi-open file |
| Bishop Pair | +30 bonus for having both bishops |
| King Pawn Shield | Bonus for pawns protecting the king |

### Move Correctness
Verified with Perft testing:

| Depth | Expected | Result |
|---|---|---|
| 1 | 20 | ✅ 20 |
| 2 | 400 | ✅ 400 |
| 3 | 8,902 | ✅ 8,902 |
| 4 | 197,281 | ✅ 197,281 |

---

## Architecture

### Backend — Spring Boot (Java)
```
src/
├── game/
│   ├── Board.java              # Board state, inCheck, en passant tracking
│   ├── Piece.java              # Abstract base class
│   ├── Position.java           # Cached position objects (flyweight pattern)
│   ├── Movement.java           # Interface — move(), undo(), isValidMove(), getAllMoves()
│   ├── MoveState.java          # Captures all state needed for make/unmake
│   └── pieces/
│       ├── Pawn.java
│       ├── Knight.java
│       ├── Bishop.java
│       ├── Rook.java
│       ├── Queen.java
│       └── King.java
├── engine/
│   ├── Search.java             # Minimax + alpha-beta + move ordering
│   ├── BoardEvaluator.java     # Static evaluation function
│   ├── BoardInitializer.java   # Board setup + FEN generation
│   ├── TranspositionTable.java # TT with flag-based cache entries
│   └── HelperConstants.java    # PST tables + material values
└── ChessController.java        # REST API endpoints
```

### Frontend — HTML + JavaScript
- **chessboard.js** for rendering
- Click-to-move interface with highlighted valid moves
- Green dots for quiet moves, red dots for captures
- Thinking overlay while engine computes
- Checkmate and stalemate detection with status messages
- Reset button

---

## REST API

| Method | Endpoint | Description |
|---|---|---|
| GET | `/chess/test` | Health check |
| POST | `/chess/reset` | Reset board to starting position |
| GET | `/chess/moves?square=e2` | Get valid moves for piece on square |
| POST | `/chess/move` | Make a human move |
| GET | `/chess/best-move-black` | Engine computes and plays best black move |

### Example `/chess/move` request
```json
{
  "from": "e2",
  "to": "e4"
}
```

### Example response
```json
{
  "valid": true,
  "fen": "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w - - 0 1"
}
```

---

## Key Design Decisions

### Make/Unmake over Board Copying
Instead of deep-copying the board for every move simulation, pieces are mutated in place via `move()` and restored via `undo()`. This eliminated ~75% of object allocations and gave a **3.7x speedup** on perft benchmarks.

### `move()` Always Returns MoveState
Every piece's `move()` captures all state that changes — captured piece, en passant target, `hasMoved` flags, rook positions for castling — into a `MoveState` object. `undo()` uses this to restore the board exactly.

### Position Cache
`Position.of(file, rank)` returns cached `Position` objects — no allocation for repeated lookups.

### `skipCastling` Flag on King
`Board.inCheck()` calls `getAllMoves` on all opponent pieces. When called on the King, castling legality checks would call `inCheck` again — causing infinite recursion. The `skipCastling` flag breaks this cycle.

---

## Performance

Tested at depth 4 from starting position:

| Metric | Value |
|---|---|
| Perft nodes | 197,281 |
| Perft time | ~1,682ms |
| Search nodes (typical midgame) | 40,000 – 150,000 |
| Search time (typical midgame) | 3 – 10 seconds |

---

## Getting Started

### Prerequisites
- Java 17+
- Maven

### Run
```bash
mvn spring-boot:run
```

Then open `http://localhost:8080` in your browser.

### Run Perft
In `Main.java`, comment out `SpringApplication.run` and uncomment:
```java
Board brd = BoardInitializer.initializeBoard();
System.out.println(perft(brd, 4, Color.WHITE)); // expected: 197281
```

---

## Tech Stack

- **Java 24**
- **Spring Boot 3.4.5**
- **chessboard.js 1.0.0**
- **jQuery 3.6.0**

---
