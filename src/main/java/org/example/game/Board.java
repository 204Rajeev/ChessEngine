package org.example.game;

import org.example.game.pieces.King;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private Map<Position, Piece> boardState= new HashMap<Position, Piece>();
    private Position enPassantTarget = null; // add to Board

    public Position getEnPassantTarget() { return enPassantTarget; }
    public void setEnPassantTarget(Position pos) {
        this.enPassantTarget = pos;
    }

    public boolean isEnPassantTarget(Position pos, Color attackingColor) {
        return enPassantTarget != null && enPassantTarget.equals(pos);
    }

    public Board(){}

    public Board(Board other) {
        this.boardState = new HashMap<>();
        this.enPassantTarget = other.enPassantTarget;
        for(Map.Entry<Position, Piece> entry : other.boardState.entrySet()) {
            Piece piece = entry.getValue();
            this.boardState.put(entry.getKey(), piece == null ? null : piece.copy());
        }
    }


    public void printBoradState(){
        System.out.println(boardState);
    }

    public boolean isPositionValidAndEmpty(char file, int rank){
        // shift to fit the coordinate system
        int x = file - 'a';
        int y = rank - 1;
        if(x >= 8 || x < 0 || y >= 8|| y < 0) return false;// out of bounds coordinate
        return this.boardState.get(getPosition(file, rank)) == null;
    }

    public Position getPosition(char file, int rank){
        return Position.of(file, rank);
    }

    public void placePiece(Position pos, Piece piece) {
        boardState.put(pos, piece);
    }

    public Piece getPiece(Position pos) {
        return boardState.get(pos);
    }

    public Map<Position, Piece> getBoardState() {
        return boardState;
    }

    public Position getPositionOfKing(Color col){
        for(Position pos : boardState.keySet()){
            Piece piece = boardState.get(pos);
            if(piece != null && piece.getColor() == col && (piece instanceof King)) return pos;
        }
        return null;
    }



    public boolean inCheck(Color color){
        /*
        * if king with this color then return true
        * else return false
        *
        * now the challenge is how to know if the king is in chekc ?
        *
        * for all the opposite color pices get their possible moves and check
        * if any of them can attack this king
        * */

        Position posOfKing = getPositionOfKing(color);
        if(posOfKing == null) return false;

        for(Position pos : boardState.keySet()){
            Piece piece = boardState.get(pos);
            if(piece == null || piece.getColor() == color) continue; // skip for same color or if position is empty

            List<Position> moves;
            if (piece instanceof King k) {
                moves = k.getAllMoves(this, pos, true);
            } else {
                moves = piece.getAllMoves(this, pos);
            }

            for(Position opponentMove : moves){
                if(opponentMove.equals(posOfKing)) return true;
            }

        }
        return false;
    }

    @Override
    public int hashCode() {
        return boardState.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Board other)) return false;
        return boardState.equals(other.boardState);
    }



}
