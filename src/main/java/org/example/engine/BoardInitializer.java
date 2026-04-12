package org.example.engine;

import org.example.game.Board;
import org.example.game.Color;
import org.example.game.Piece;
import org.example.game.Position;
import org.example.game.pieces.*;

public class BoardInitializer {


    public static String boardHash(Board board) {
        return String.valueOf(board.hashCode());
    }

    public static String generateFen(Board board) {
        StringBuilder fen = new StringBuilder();

        for (int rank = 8; rank >= 1; rank--) {
            int empty = 0;

            for (char file = 'a'; file <= 'h'; file++) {
                Position pos = Position.of(file, rank);
                Piece piece = board.getPiece(pos);

                if (piece == null) {
                    empty++;
                } else {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append(getFenChar(piece));
                }
            }

            if (empty > 0) fen.append(empty);
            if (rank > 1) fen.append("/");
        }

        fen.append(" w - - 0 1"); // simplified
        return fen.toString();
    }

    public static char getFenChar(Piece piece) {

        char c;

        if (piece instanceof Pawn) c = 'p';
        else if (piece instanceof Rook) c = 'r';
        else if (piece instanceof Knight) c = 'n';
        else if (piece instanceof Bishop) c = 'b';
        else if (piece instanceof Queen) c = 'q';
        else if (piece instanceof King) c = 'k';
        else c = ' ';


        if (piece.getColor() == Color.WHITE) {
            return Character.toUpperCase(c);
        } else {
            return c;
        }
    }

    public static Board initializeBoard() {
        Board board = new Board();

        // place pawns
        for (char file = 'a'; file <= 'h'; file++) {
            board.placePiece(new Position(file, 2), new Pawn(new Position(file, 2), Color.WHITE));
            board.placePiece(new Position(file, 7), new Pawn(new Position(file, 7), Color.BLACK));
        }

        // place rooks
        board.placePiece(new Position('a', 1), new Rook(new Position('a', 1), Color.WHITE));
        board.placePiece(new Position('h', 1), new Rook(new Position('h', 1), Color.WHITE));
        board.placePiece(new Position('a', 8), new Rook(new Position('a', 8), Color.BLACK));
        board.placePiece(new Position('h', 8), new Rook(new Position('h', 8), Color.BLACK));

        // place knights
        board.placePiece(new Position('b', 1), new Knight(new Position('b', 1), Color.WHITE));
        board.placePiece(new Position('g', 1), new Knight(new Position('g', 1), Color.WHITE));
        board.placePiece(new Position('b', 8), new Knight(new Position('b', 8), Color.BLACK));
        board.placePiece(new Position('g', 8), new Knight(new Position('g', 8), Color.BLACK));

        // place bishops
        board.placePiece(new Position('c', 1), new Bishop(new Position('c', 1), Color.WHITE));
        board.placePiece(new Position('f', 1), new Bishop(new Position('f', 1), Color.WHITE));
        board.placePiece(new Position('c', 8), new Bishop(new Position('c', 8), Color.BLACK));
        board.placePiece(new Position('f', 8), new Bishop(new Position('f', 8), Color.BLACK));

        // place queens
        board.placePiece(new Position('d', 1), new Queen(new Position('d', 1), Color.WHITE));
        board.placePiece(new Position('d', 8), new Queen(new Position('d', 8), Color.BLACK));

        // place kings
        board.placePiece(new Position('e', 1), new King(new Position('e', 1), Color.WHITE));
        board.placePiece(new Position('e', 8), new King(new Position('e', 8), Color.BLACK));

        return board;
    }
}
