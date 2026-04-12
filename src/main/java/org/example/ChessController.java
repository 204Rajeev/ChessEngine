package org.example;

import org.example.engine.BoardEvaluator;
import org.example.engine.BoardInitializer;
import org.example.engine.Search;
import org.example.game.*;
import org.example.game.pieces.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chess")
@CrossOrigin(origins = "*")
public class ChessController {

    private Board board = BoardInitializer.initializeBoard();

    @GetMapping("/test")
    public String ping() {
        return "Chess backend running";
    }

    @PostMapping("/reset")
    public Map<String, Object> reset() {
        board = BoardInitializer.initializeBoard();
        return Map.of(
                "valid", true,
                "fen", BoardInitializer.generateFen(board)
        );
    }

    @GetMapping("/best-move-black")
    public Map<String, Object> getBestBlack() {
        Board bestBoard = Search.getBestMove(board, 4, Color.BLACK);

        if (bestBoard == null) {
            boolean inCheck = board.inCheck(Color.BLACK);
            return Map.of(
                    "valid", false,
                    "fen", BoardInitializer.generateFen(board),
                    "status", inCheck ? "checkmate" : "stalemate"
            );
        }

        System.out.println("White score: " + BoardEvaluator.score(board));
        board = bestBoard;
        return Map.of("valid", true, "fen", BoardInitializer.generateFen(board), "status", "ok");
    }

    @GetMapping("/moves")
    public List<String> getMoves(@RequestParam String square) {
        Position position = inputPos(square);
        if (position == null) return List.of();

        Piece p = board.getPiece(position);
        if (p == null) return List.of();
        if (!(p instanceof Movement m)) return List.of();

        List<String> result = new ArrayList<>();
        for (Position pos : p.getAllMoves(board, position)) {
            if (!m.isValidMove(board, position, pos)) continue;
            Piece target = board.getPiece(pos);
            boolean isEnPassant = (p instanceof Pawn) && board.isEnPassantTarget(pos, p.getColor());
            String suffix = (isEnPassant || (target != null && target.getColor() != p.getColor())) ? "#" : "@";
            result.add(pos.toString() + suffix);
        }
        return result;
    }

    @PostMapping("/move")
    public Map<String, Object> makeMove(@RequestBody Map<String, String> req) {
        String from = req.get("from").substring(0, 2);
        String to   = req.get("to").substring(0, 2);

        Position fromPos = inputPos(from);
        Position toPos   = inputPos(to);

        if (fromPos == null || toPos == null) return Map.of("valid", false, "fen", "");

        Piece p = board.getPiece(fromPos);
        if (!(p instanceof Movement m)) return Map.of("valid", false, "fen", "");

        boolean valid = p.getAllMoves(board, fromPos)
                .stream()
                .filter(pos -> m.isValidMove(board, fromPos, pos))
                .anyMatch(toPos::equals);

        if (valid) {
            m.move(board, fromPos, toPos);
        }

        return Map.of("valid", valid, "fen", BoardInitializer.generateFen(board));
    }

    private Position inputPos(String loc) {
        if (loc == null || loc.length() != 2) return null;

        char file = Character.toLowerCase(loc.charAt(0));
        char rankChar = loc.charAt(1);

        if (file < 'a' || file > 'h') return null;
        if (rankChar < '1' || rankChar > '8') return null;

        int rank = rankChar - '0';
        return Position.of(file, rank);
    }
}