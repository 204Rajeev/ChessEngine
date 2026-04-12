package org.example.game;

public class MoveState {
    public final Piece movedPiece;
    public final Piece capturedPiece;
    public final Position enPassantTarget;
    public final Position epCapturedPos;
    public final Piece epCapturedPiece;
    public final boolean savedIsFirstMove; // ← pawn specific
    public final Position rookFrom;
    public final Position rookTo;
    public final Piece savedRook;

    public MoveState(Piece movedPiece, Piece capturedPiece,
                     Position enPassantTarget,
                     Position epCapturedPos, Piece epCapturedPiece,
                     boolean savedIsFirstMove,
                     Position rookFrom, Position rookTo, Piece savedRook) {
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.enPassantTarget = enPassantTarget;
        this.epCapturedPos = epCapturedPos;
        this.epCapturedPiece = epCapturedPiece;
        this.savedIsFirstMove = savedIsFirstMove;
        this.rookFrom = rookFrom;
        this.rookTo = rookTo;
        this.savedRook = savedRook;
    }
}