package org.example.engine;

import org.example.game.Piece;
import org.example.game.pieces.*;

public class HelperConstants {
    public static int getMaterialValue(Piece p) {
        if (p instanceof Queen)  return 900;
        if (p instanceof Rook)   return 500;
        if (p instanceof Bishop) return 300;
        if (p instanceof Knight) return 310;
        if (p instanceof Pawn)   return 100;
        return 0;
    }

    /********* PST *************/

    // written form white perspective
    public static final int[][] PAWN_PST = {
            {  0,  0,  0,  0,  0,  0,  0,  0 },
            { 50, 50, 50, 50, 50, 50, 50, 50 },
            { 10, 10, 20, 30, 30, 20, 10, 10 },
            {  5,  5, 10, 25, 25, 10,  5,  5 },
            {  0,  0,  0, 20, 20,  0,  0,  0 },
            {  5, -5,-10,  0,  0,-10, -5,  5 },
            {  5, 10, 10,-20,-20, 10, 10,  5 },
            {  0,  0,  0,  0,  0,  0,  0,  0 },
    };

    public static final int[][] KNIGHT_PST = {
            { -50,-40,-30,-30,-30,-30,-40,-50 }, // rank 8
            { -40,-20,  0,  0,  0,  0,-20,-40 }, // rank 7
            { -30,  0, 10, 15, 15, 10,  0,-30 }, // rank 6
            { -30,  5, 15, 20, 20, 15,  5,-30 }, // rank 5
            { -30,  0, 15, 20, 20, 15,  0,-30 }, // rank 4
            { -30,  5, 10, 15, 15, 10,  5,-30 }, // rank 3
            { -40,-20,  0,  5,  5,  0,-20,-40 }, // rank 2
            { -50,-40,-30,-30,-30,-30,-40,-50 }, // rank 1
    };

    public static final int[][] QUEEN_PST = {
            { -20,-10,-10, -5, -5,-10,-10,-20 }, // rank 8
            { -10,  0,  0,  0,  0,  0,  0,-10 }, // rank 7
            { -10,  0,  5,  5,  5,  5,  0,-10 }, // rank 6
            {  -5,  0,  5,  5,  5,  5,  0, -5 }, // rank 5
            {   0,  0,  5,  5,  5,  5,  0, -5 }, // rank 4
            { -10,  5,  5,  5,  5,  5,  0,-10 }, // rank 3
            { -10,  0,  5,  0,  0,  0,  0,-10 }, // rank 2
            { -20,-10,-10, -5, -5,-10,-10,-20 }, // rank 1
    };

    public static final int[][] ROOK_PST = {
            {  0,  0,  0,  0,  0,  0,  0,  0 }, // rank 8
            {  5, 10, 10, 10, 10, 10, 10,  5 }, // rank 7
            { -5,  0,  0,  0,  0,  0,  0, -5 }, // rank 6
            { -5,  0,  0,  0,  0,  0,  0, -5 }, // rank 5
            { -5,  0,  0,  0,  0,  0,  0, -5 }, // rank 4
            { -5,  0,  0,  0,  0,  0,  0, -5 }, // rank 3
            { -5,  0,  0,  0,  0,  0,  0, -5 }, // rank 2
            {  0,  0,  0,  5,  5,  0,  0,  0 }, // rank 1
    };

    public static final int[][] BISHOP_PST = {
            { -20,-10,-10,-10,-10,-10,-10,-20 }, // rank 8
            { -10,  0,  0,  0,  0,  0,  0,-10 }, // rank 7
            { -10,  0,  5, 10, 10,  5,  0,-10 }, // rank 6
            { -10,  5,  5, 10, 10,  5,  5,-10 }, // rank 5
            { -10,  0, 10, 10, 10, 10,  0,-10 }, // rank 4
            { -10, 10, 10, 10, 10, 10, 10,-10 }, // rank 3
            { -10,  5,  0,  0,  0,  0,  5,-10 }, // rank 2
            { -20,-10,-10,-10,-10,-10,-10,-20 }, // rank 1
    };


}
