package org.example.game.pieces;

import org.example.game.*;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece implements Movement {
    private static final int[] dfile = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] drank = {-1, 0, 1, -1, 1, -1, 0, 1};

    public King(Position pos, Color color) {
        super(pos, color);
    }

    private boolean hasMoved = false;

    boolean canCastleKingSide(Board board) {
        if (hasMoved) return false;

        Piece kngRook = this.getColor() == Color.WHITE
                ? board.getPiece(Position.of('h', 1))
                : board.getPiece(Position.of('h', 8));

        if (!(kngRook instanceof Rook r)) return false;
        if (r.isHasMoved()) return false;

        Piece p1 = this.getColor() == Color.WHITE
                ? board.getPiece(Position.of('f', 1))
                : board.getPiece(Position.of('f', 8));
        Piece p2 = this.getColor() == Color.WHITE
                ? board.getPiece(Position.of('g', 1))
                : board.getPiece(Position.of('g', 8));

        if (p1 != null || p2 != null) return false;

        Position kingPos  = this.getColor() == Color.WHITE ? Position.of('e', 1) : Position.of('e', 8);
        Position interPos = this.getColor() == Color.WHITE ? Position.of('f', 1) : Position.of('f', 8);
        Position destPos  = this.getColor() == Color.WHITE ? Position.of('g', 1) : Position.of('g', 8);

        if (board.inCheck(this.getColor())) return false;
        if (!isValidMove(board, kingPos, interPos)) return false;
        return isValidMove(board, kingPos, destPos);
    }

    boolean canCastleQueenSide(Board board) {
        if (hasMoved) return false;

        Piece queenRook = this.getColor() == Color.WHITE
                ? board.getPiece(Position.of('a', 1))
                : board.getPiece(Position.of('a', 8));

        if (!(queenRook instanceof Rook r)) return false;
        if (r.isHasMoved()) return false;

        Piece p1 = this.getColor() == Color.WHITE
                ? board.getPiece(Position.of('b', 1))
                : board.getPiece(Position.of('b', 8));
        Piece p2 = this.getColor() == Color.WHITE
                ? board.getPiece(Position.of('c', 1))
                : board.getPiece(Position.of('c', 8));
        Piece p3 = this.getColor() == Color.WHITE
                ? board.getPiece(Position.of('d', 1))
                : board.getPiece(Position.of('d', 8));

        if (p1 != null || p2 != null || p3 != null) return false;

        Position kingPos  = this.getColor() == Color.WHITE ? Position.of('e', 1) : Position.of('e', 8);
        Position interPos = this.getColor() == Color.WHITE ? Position.of('d', 1) : Position.of('d', 8);
        Position destPos  = this.getColor() == Color.WHITE ? Position.of('c', 1) : Position.of('c', 8);

        if (board.inCheck(this.getColor())) return false;
        if (!isValidMove(board, kingPos, interPos)) return false;
        return isValidMove(board, kingPos, destPos);
    }

    @Override
    public MoveState move(Board board, Position start, Position end) {
        Piece capturedPiece = board.getPiece(end);
        Position savedEpTarget = board.getEnPassantTarget();

        // save rook state for castling undo
        Position rookFrom = null, rookTo = null;
        Piece savedRook = null;
        int rank = start.getRank();

        board.placePiece(start, null);
        this.hasMoved = true;
        board.placePiece(end, this);

        // kingside castling
        if (end.getFile() - start.getFile() == 2) {
            rookFrom = board.getPosition('h', rank);
            rookTo   = board.getPosition('f', rank);
            savedRook = board.getPiece(rookFrom);
            board.placePiece(rookFrom, null);
            Rook movedRook = new Rook(rookTo, this.getColor());
            movedRook.setHasMoved(true);
            board.placePiece(rookTo, movedRook);
        }

        // queenside castling
        if (start.getFile() - end.getFile() == 2) {
            rookFrom = board.getPosition('a', rank);
            rookTo   = board.getPosition('d', rank);
            savedRook = board.getPiece(rookFrom);
            board.placePiece(rookFrom, null);
            Rook movedRook = new Rook(rookTo, this.getColor());
            movedRook.setHasMoved(true);
            board.placePiece(rookTo, movedRook);
        }

        board.setEnPassantTarget(null);

        return new MoveState(this, capturedPiece, savedEpTarget,
                null, null, false, rookFrom, rookTo, savedRook);
//                  ↑ add false — King never uses savedIsFirstMove
    }

    @Override
    public void undo(Board board, Position start, Position end, MoveState state) {
        this.hasMoved = false;              // restore hasMoved
        board.placePiece(start, this);      // restore king to start
        board.placePiece(end, state.capturedPiece); // restore captured piece
        board.setEnPassantTarget(state.enPassantTarget); // restore ep target

        // restore rook if castling occurred
        if (state.rookFrom != null) {
            board.placePiece(state.rookTo, null);
            board.placePiece(state.rookFrom, state.savedRook);
        }
    }

    @Override
    public boolean isValidMove(Board board, Position current, Position end) {
        if (!end.isInBounds()) return false;
        Piece target = board.getPiece(end);
        if (target != null && target.getColor() == this.getColor()) return false;

        MoveState state = move(board, current, end);
        boolean legal = !board.inCheck(this.getColor());
        undo(board, current, end, state);
        return legal;
    }

    @Override
    public List<Position> getAllMoves(Board board, Position current) {
        return getAllMoves(board, current, false);
    }

    public List<Position> getAllMoves(Board board, Position current, boolean skipCastling) {
        List<Position> pos = new ArrayList<>();

        int rank = current.getRank();
        char file = current.getFile();

        for (int i = 0; i < 8; i++) {
            Position candidate = board.getPosition((char)(file + dfile[i]), rank + drank[i]);
            if (candidate == null || !candidate.isInBounds()) continue;
            Piece target = board.getPiece(candidate);
            if (target == null || target.getColor() != this.getColor()) {
                pos.add(candidate);
            }
        }

        if (!skipCastling) {
            if (canCastleKingSide(board)) {
                pos.add(this.getColor() == Color.WHITE ? Position.of('g', 1) : Position.of('g', 8));
            }
            if (canCastleQueenSide(board)) {
                pos.add(this.getColor() == Color.WHITE ? Position.of('c', 1) : Position.of('c', 8));
            }
        }

        return pos;
    }

    @Override
    public Piece copy() {
        King copy = new King(this.getPos(), this.getColor());
        copy.hasMoved = this.hasMoved;
        return copy;
    }

    @Override
    public void capture(Board board, Position current, Position end) {}
}