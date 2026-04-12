package org.example.game.pieces;

import org.example.game.*;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece implements Movement {

    private boolean isFirstMove = true;

    public Pawn(Position pos, Color color) {
        super(pos, color);
    }

    private void addStepMove(Board board, char file, int rank, List<Position> positions, int step) {
        int direction = this.getColor() == Color.BLACK ? -step : step;
        int direction2 = this.getColor() == Color.BLACK ? -(step - 1) : (step - 1);
        if (step == 2) {
            if (board.isPositionValidAndEmpty(file, rank + direction2)
                    && board.isPositionValidAndEmpty(file, rank + direction)) {
                positions.add(board.getPosition(file, rank + direction));
            }
        } else {
            if (board.isPositionValidAndEmpty(file, rank + direction)) {
                positions.add(board.getPosition(file, rank + direction));
            }
        }
    }

    @Override
    public List<Position> getAllMoves(Board board, Position current) {
        List<Position> positions = new ArrayList<>();
        char file = current.getFile();
        int rank = current.getRank();

        if (this.isFirstMove) {
            addStepMove(board, file, rank, positions, 2);
            addStepMove(board, file, rank, positions, 1);
        } else {
            addStepMove(board, file, rank, positions, 1);
        }

        int captureRank = this.getColor() == Color.WHITE ? rank + 1 : rank - 1;
        Position rightDia = board.getPosition((char)(file + 1), captureRank);
        Position leftDia  = board.getPosition((char)(file - 1), captureRank);

        if (rightDia != null && rightDia.isInBounds() && board.getPiece(rightDia) != null
                && board.getPiece(rightDia).getColor() != this.getColor()) {
            positions.add(rightDia);
        }
        if (leftDia != null && leftDia.isInBounds() && board.getPiece(leftDia) != null
                && board.getPiece(leftDia).getColor() != this.getColor()) {
            positions.add(leftDia);
        }

        positions.addAll(enPassant(board, current));
        return positions;
    }

    private List<Position> enPassant(Board board, Position current) {
        List<Position> res = new ArrayList<>();
        char file = current.getFile();
        int rank = current.getRank();
        int captureRank = this.getColor() == Color.WHITE ? rank + 1 : rank - 1;

        Position rightDia = board.getPosition((char)(file + 1), captureRank);
        Position leftDia  = board.getPosition((char)(file - 1), captureRank);

        if (rightDia != null && board.isEnPassantTarget(rightDia, this.getColor())) res.add(rightDia);
        if (leftDia  != null && board.isEnPassantTarget(leftDia,  this.getColor())) res.add(leftDia);
        return res;
    }

    @Override
    public MoveState move(Board board, Position start, Position end) {
        // --- save state before any mutation ---
        boolean savedIsFirstMove = this.isFirstMove;
        Piece capturedPiece = board.getPiece(end);
        Position savedEpTarget = board.getEnPassantTarget();
        Position epCapturedPos = null;
        Piece epCapturedPiece = null;

        board.placePiece(start, null);

        // --- en passant capture ---
        if (board.isEnPassantTarget(end, this.getColor())) {
            epCapturedPos = board.getPosition(end.getFile(), start.getRank());
            epCapturedPiece = board.getPiece(epCapturedPos);
            board.placePiece(epCapturedPos, null);
        }

        // --- update ep target ---
        board.setEnPassantTarget(null);
        if (Math.abs(end.getRank() - start.getRank()) == 2) {
            int epRank = this.getColor() == Color.WHITE
                    ? start.getRank() + 1
                    : start.getRank() - 1;
            board.setEnPassantTarget(board.getPosition(start.getFile(), epRank));
        }

        // --- place this pawn at end ---
        this.isFirstMove = false; // mutate this, undo will restore
        board.placePiece(end, this);

        // --- promotion ---
        boolean onLastRank = this.getColor() == Color.WHITE && end.getRank() == 8
                || this.getColor() == Color.BLACK && end.getRank() == 1;
        if (onLastRank) board.placePiece(end, new Queen(end, this.getColor()));

        return new MoveState(this, capturedPiece, savedEpTarget,
                epCapturedPos, epCapturedPiece,
                savedIsFirstMove,  // ← pawn-specific
                null, null, null); // no rook fields
    }

    @Override
    public void undo(Board board, Position start, Position end, MoveState state) {
        this.isFirstMove = state.savedIsFirstMove; // restore first move flag
        board.placePiece(end, state.capturedPiece); // restore captured piece (or null)
        board.placePiece(start, this);              // restore pawn to start
        board.setEnPassantTarget(state.enPassantTarget); // restore ep target

        // restore en passant captured pawn
        if (state.epCapturedPos != null) {
            board.placePiece(state.epCapturedPos, state.epCapturedPiece);
        }
    }

    @Override
    public boolean isValidMove(Board board, Position current, Position end) {
        if (!end.isInBounds()) return false;
        Piece target = board.getPiece(end);
        if (target != null && target.getColor() == this.getColor()) return false;

        MoveState state = move(board, current, end);    // make
        boolean legal = !board.inCheck(this.getColor());
        undo(board, current, end, state);               // unmake
        return legal;
    }

    @Override
    public Piece copy() {
        Pawn copy = new Pawn(this.getPos(), this.getColor());
        if (!this.isFirstMove) copy.setFirstMoveDone();
        return copy;
    }

    @Override
    public void capture(Board board, Position current, Position end) {}

    public void setFirstMoveDone() {
        this.isFirstMove = false;
    }
}