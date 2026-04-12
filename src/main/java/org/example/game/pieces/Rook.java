package org.example.game.pieces;

import org.example.game.*;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece implements Movement {

    private static final int[] dfile = {0, 0, 1, -1};
    private static final int[] drank = {1, -1, 0, 0};
    private boolean hasMoved = false;

    public Rook(Position pos, Color col) {
        super(pos, col);
    }

    @Override
    public List<Position> getAllMoves(Board board, Position current) {
        List<Position> positions = new ArrayList<>();
        char file = current.getFile();
        int rank = current.getRank();

        for (int i = 0; i < 4; i++) {
            char newFile = (char)(file + dfile[i]);
            int newRank = rank + drank[i];

            while (true) {
                Position candidate = board.getPosition(newFile, newRank);
                if (candidate == null || !candidate.isInBounds()) break;

                Piece target = board.getPiece(candidate);
                if (target == null) {
                    positions.add(candidate);
                } else if (target.getColor() != this.getColor()) {
                    positions.add(candidate);
                    break;
                } else {
                    break;
                }

                newFile = (char)(newFile + dfile[i]);
                newRank = newRank + drank[i];
            }
        }
        return positions;
    }

    @Override
    public MoveState move(Board board, Position start, Position end) {
        boolean savedHasMoved = this.hasMoved; // save before mutation
        Piece capturedPiece = board.getPiece(end);
        Position savedEpTarget = board.getEnPassantTarget();

        board.placePiece(start, null);
        this.hasMoved = true; // mutate this, undo will restore
        board.placePiece(end, this);
        board.setEnPassantTarget(null);

        return new MoveState(this, capturedPiece, savedEpTarget,
                null, null, savedHasMoved, null, null, null);
        //                         ↑ reusing savedIsFirstMove field for savedHasMoved
    }

    @Override
    public void undo(Board board, Position start, Position end, MoveState state) {
        this.hasMoved = state.savedIsFirstMove; // restore hasMoved
        board.placePiece(end, state.capturedPiece);
        board.placePiece(start, this);
        board.setEnPassantTarget(state.enPassantTarget);
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
    public Piece copy() {
        Rook copy = new Rook(this.getPos(), this.getColor());
        copy.setHasMoved(this.hasMoved);
        return copy;
    }

    @Override
    public void capture(Board board, Position current, Position end) {}

    public boolean isHasMoved() { return hasMoved; }
    public void setHasMoved(boolean val) { this.hasMoved = val; }
}