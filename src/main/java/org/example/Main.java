package org.example;

import org.example.engine.BoardInitializer;
import org.example.engine.Search;
import org.example.game.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    static long nodeCount = 0;

    public static long perft(Board board, int depth, Color color) {
        if (depth == 0) {
            nodeCount++;
            return 1;
        }
        long nodes = 0;
        for (Board next : Search.getNextBoards(board, color)) {
            nodes += perft(next, depth - 1, opposite(color));
        }
        return nodes;
    }

    public static Color opposite(Color col) {
        return col == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

//        Board brd = BoardInitializer.initializeBoard();
//
//        long start = System.currentTimeMillis();
//        long result = perft(brd, 4, Color.WHITE);
//        long end = System.currentTimeMillis();
//
//        System.out.println("Perft(4): " + result);
//        System.out.println("Nodes:    " + nodeCount);
//        System.out.println("Time:     " + (end - start) + "ms");
    }
}