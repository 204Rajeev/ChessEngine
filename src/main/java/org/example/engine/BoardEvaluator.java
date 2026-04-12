package org.example.engine;

import org.example.game.Board;
import org.example.game.Color;
import org.example.game.Piece;
import org.example.game.Position;
import org.example.game.pieces.*;

import java.util.Map;

public class BoardEvaluator {

    // piece-based point evalation evaluation
    // position-based evaluation
    // pawn passed
    // piece square table

    public static double score(Board board) {
        double material = 0, pst = 0, mobility = 0;
        int whiteBishops = 0, blackBishops = 0;
        Map<Position, Piece> map = board.getBoardState();

        for (Position pos : map.keySet()) {
            Piece p = map.get(pos);
            if (p == null || p instanceof King) continue;

            int sign = p.getColor() == Color.WHITE ? 1 : -1;
            int row = p.getColor() == Color.WHITE ? 8 - pos.getRank() : pos.getRank() - 1;
            int col = pos.getFile() - 'a';

            // --- material ---
            material += sign * HelperConstants.getMaterialValue(p);

            // --- PST ---
            if      (p instanceof Pawn)   pst += sign * HelperConstants.PAWN_PST[row][col];
            else if (p instanceof Knight) pst += sign * HelperConstants.KNIGHT_PST[row][col];
            else if (p instanceof Bishop) pst += sign * HelperConstants.BISHOP_PST[row][col];
            else if (p instanceof Rook)   pst += sign * HelperConstants.ROOK_PST[row][col];
            else if (p instanceof Queen)  pst += sign * HelperConstants.QUEEN_PST[row][col];

            // --- mobility ---
            mobility += sign * p.getAllMoves(board, pos).size();

            // --- bishop pair count ---
            if (p instanceof Bishop) {
                if (p.getColor() == Color.WHITE) whiteBishops++;
                else blackBishops++;
            }
        }

        // --- passed pawn + rook open file (separate loop but only over pawns/rooks) ---
        double passedPawn = passedPawnBonus(board);
        double rookFile   = rookOpenFileBonus(board);

        // --- bishop pair ---
        double bishopPair = 0;
        if (whiteBishops >= 2) bishopPair += 30;
        if (blackBishops >= 2) bishopPair -= 30;

        // --- king pawn shield (unavoidable separate pass) ---
        double kingShield = kingPawnShield(board);

        double res = material + pst + (mobility * 0.1) + passedPawn + rookFile + bishopPair + kingShield;
//        System.out.printf("material=%.2f pst=%.2f mobility=%.2f passedPawn=%.2f rookFile=%.2f bishopPair=%.2f kingShield=%.2f total=%.2f%n",
//                material, pst, (mobility * 0.1), passedPawn, rookFile, bishopPair, kingShield, res);
        return res;
    }

    // PST Evaluation

    // passed pawn evaluation
    private static boolean isPassedPawn(Board board, char file, int rank, Color color) {
        int start = color == Color.WHITE ? rank + 1 : rank - 1;
        int end   = color == Color.WHITE ? 7 : 2;
        int step  = color == Color.WHITE ? 1 : -1;
        Color enemy = color == Color.WHITE ? Color.BLACK : Color.WHITE;

        for (int r = start; color == Color.WHITE ? r <= end : r >= end; r += step) {
            for (int df = -1; df <= 1; df++) {
                char f = (char)(file + df);
                if (f < 'a' || f > 'h') continue;
                Piece target = board.getPiece(Position.of(f, r));
                if (target instanceof Pawn && target.getColor() == enemy) return false;
            }
        }
        return true;
    }

    private static double passedPawnBonus(Board board) {
        double sum = 0;

        int[] whiteBonusByRank = { 0, 10, 20, 30, 50, 75, 100, 0 };
        int[] blackBonusByRank = { 0, 100, 75, 50, 30, 20, 10,  0 };

        Map<Position, Piece> map = board.getBoardState();
        for (Position pos : map.keySet()) {
            Piece p = map.get(pos);
            if (!(p instanceof Pawn)) continue;

            char file = pos.getFile();
            int rank = pos.getRank();

            if (isPassedPawn(board, file, rank, p.getColor())) {
                int sign = p.getColor() == Color.WHITE ? 1 : -1;
                int bonus = p.getColor() == Color.WHITE
                        ? whiteBonusByRank[rank - 1]
                        : blackBonusByRank[rank - 1];
                sum += sign * bonus;
            }
        }
        return sum;
    }


    private static double rookOpenFileBonus(Board board) {
        double sum = 0;
        Map<Position, Piece> map = board.getBoardState();
        for (Position pos : map.keySet()) {
            Piece p = map.get(pos);
            if (!(p instanceof Rook)) continue;
            int sign = p.getColor() == Color.WHITE ? 1 : -1;
            char file = pos.getFile();
            boolean friendlyPawn = false, enemyPawn = false;
            for (int rank = 1; rank <= 8; rank++) {
                Piece target = board.getPiece(Position.of(file, rank));
                if (!(target instanceof Pawn)) continue;
                if (target.getColor() == p.getColor()) friendlyPawn = true;
                else enemyPawn = true;
            }
            if (!friendlyPawn && !enemyPawn) sum += sign * 25;
            else if (!friendlyPawn) sum += sign * 15;
        }
        return sum;
    }

    private static double kingPawnShield(Board board) {
        double sum = 0;
        for (Color color : Color.values()) {
            int sign = color == Color.WHITE ? 1 : -1;
            Position kingPos = board.getPositionOfKing(color);
            if (kingPos == null) continue;
            int shieldRank = color == Color.WHITE ? kingPos.getRank() + 1 : kingPos.getRank() - 1;
            for (int df = -1; df <= 1; df++) {
                char f = (char)(kingPos.getFile() + df);
                if (f < 'a' || f > 'h') continue;
                Piece shield = board.getPiece(Position.of(f, shieldRank));
                if (shield instanceof Pawn && shield.getColor() == color) sum += sign * 10;
            }
        }
        return sum;
    }

}
