package org.example.game;


import java.util.List;



public abstract class Piece {


    private final Position pos;
    private final Color color;

    public Piece(Position pos, Color color){
        this.pos = pos;
        this.color = color;
    }


    public Position getPos() {
        return pos;
    }

    public Color getColor() {
        return color;
    }

    /*
    * the method below returns the list of possible moves by the pice inside the board
    * irrespective of be resulting in a check or not
    * */
    public abstract List<Position> getAllMoves(Board board, Position current);
    public abstract Piece copy();


}



