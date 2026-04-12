package org.example.engine;

/*
 * this is the heart of the engine
 * used for searching all possibilities
 *
 * using minimax with alpha beta pruning
 * */


import org.example.game.*;
import org.example.game.pieces.Pawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.engine.HelperConstants.getMaterialValue;

public class Search {


    /*
     * use memoization to optimize the below dfs function
     * */
    private static final TranspositionTable tt = new TranspositionTable();
    static int nodeCount = 0;
    static int ttHits = 0;
    static double evalTime = 0;

    public static double miniMaxWithAlphaBetaPruning(Board current, int depth, double alpha, double beta, boolean maxPlayer) {
        nodeCount++;
        int key = current.hashCode(); // board fingerprint
        TranspositionTable.TTEntry entry = tt.get(key);

         //cache hit — only trust if stored depth >= current depth
        if (entry != null && entry.depth >= depth) {
            if (entry.flag == TranspositionTable.Flag.EXACT){
                ttHits++;
                return entry.score;
            }
            if (entry.flag == TranspositionTable.Flag.LOWER_BOUND && entry.score >= beta)
                return entry.score;
            if (entry.flag == TranspositionTable.Flag.UPPER_BOUND && entry.score <= alpha)
                return entry.score;
            // otherwise fall through and search normally
        }

        if (depth == 0) {
            long s = System.nanoTime();
            double score = BoardEvaluator.score(current);

            tt.put(key, score, 0, TranspositionTable.Flag.EXACT);
            return score;
        }

        //System.out.println("Total eval time: " + evalTime/1_000_000 + "ms");
        evalTime = 0;

        Color color = maxPlayer ? Color.WHITE : Color.BLACK;
        List<Board> nextBoards = getNextBoards(current, color);

        if (nextBoards.isEmpty()) {
            if (current.inCheck(color)) {
                return maxPlayer ? -1e10 : 1e10;
            }
            return 0.0;
        }

        double originalAlpha = alpha;
        double best;

        if (maxPlayer) {
            best = -1e10;
            for (Board next : nextBoards) {
                double eval = miniMaxWithAlphaBetaPruning(next, depth - 1, alpha, beta, false);
                best = Math.max(best, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
        } else {
            best = 1e10;
            for (Board next : nextBoards) {
                double eval = miniMaxWithAlphaBetaPruning(next, depth - 1, alpha, beta, true);
                best = Math.min(best, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
        }

        TranspositionTable.Flag flag;
        if (best <= originalAlpha) flag = TranspositionTable.Flag.UPPER_BOUND;      // failed low
        else if (best >= beta)     flag = TranspositionTable.Flag.LOWER_BOUND;      // failed high
        else                       flag = TranspositionTable.Flag.EXACT;             // exact score

        tt.put(key, best, depth, flag);
        return best;
    }

    public static List<Board> getNextBoards(Board current, Color color) {
        Map<Position, Piece> map = current.getBoardState();
        List<ScoredBoard> scored = new ArrayList<>();

        // snapshot keys to avoid ConcurrentModificationException
        List<Position> positions = new ArrayList<>(map.keySet()); // ← fix

        for (Position pos : positions) { // ← iterate snapshot, not live map
            Piece p = map.get(pos);
            if (p == null || p.getColor() != color) continue;
            if (!(p instanceof Movement m)) continue;

            for (Position move : p.getAllMoves(current, pos)) {
                if (!move.isInBounds()) continue;
                Piece target = current.getPiece(move);
                if (target != null && target.getColor() == color) continue;

                MoveState state = m.move(current, pos, move);

                if (current.inCheck(color)) {
                    m.undo(current, pos, move, state);
                    continue;
                }

                Board next = new Board(current);
                m.undo(current, pos, move, state);

                int moveScore = 0;
                if (target != null) {
                    moveScore += 100 + getMaterialValue(target) - getMaterialValue(p);
                }
                scored.add(new ScoredBoard(next, moveScore));
            }
        }

        scored.sort((a, b) -> b.score - a.score);
        List<Board> result = new ArrayList<>(scored.size());
        for (ScoredBoard sb : scored) result.add(sb.board);
        return result;
    }

    // inner class
    private static class ScoredBoard {
        Board board;
        int score;
        ScoredBoard(Board board, int score) {
            this.board = board;
            this.score = score;
        }
    }


    public static Board getBestMove(Board board, int depth, Color engineColor) {
        tt.clear();
        boolean isMax = engineColor == Color.WHITE;
        double bestScore = isMax ? -1e10 : 1e10;
        Board bestBoard = null;

        long start = System.currentTimeMillis();
        for (Board next : getNextBoards(board, engineColor)) {
            double score = miniMaxWithAlphaBetaPruning(next, depth - 1, -1e10, 1e10, !isMax);
            if (isMax ? score >= bestScore : score <= bestScore) {
                bestScore = score;
                bestBoard = next;
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
        System.out.println("Nodes searched: " + nodeCount);
        System.out.println("ttHIts : " + ttHits);
        nodeCount = 0;
        ttHits = 0;
        System.out.println("Black Score :" + bestScore);
        return bestBoard;
    }
}