package org.example.game;

import java.util.Objects;

public class Position {


    private char file; // a-h
    private int rank; // 1-8

    public Position(char file, int rank){
        this.file = file;
        this.rank = rank;
    }

    // cache of all 64 positions
    private static final Position[][] CACHE = new Position[8][8];

    static {
        for(int f = 0; f < 8; f++){
            for(int r = 0; r < 8; r++){
                CACHE[f][r] = new Position((char)('a' + f), r + 1);
            }
        }
    }


    public char getFile() {
        return file;
    }

    public int getRank() {
        return rank;
    }

    public void setFile(char file) {
        this.file = file;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public boolean isInBounds(){
        return 'a' <= this.file && this.file <= 'h' && this.rank >= 1 && this.rank <= 8;
    }

    public static Position of(char file, int rank){
        int x = file - 'a';
        int y = rank - 1;
        if(x < 0 || x >= 8 || y < 0 || y >= 8) return null; // out of bounds
        return CACHE[x][y];
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position other)) return false;
        return this.file == other.file && this.rank == other.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, rank);
    }

    @Override
    public String toString() {
        return "" + file + rank;
    }
}