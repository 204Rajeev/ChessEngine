package org.example.engine;

import java.util.HashMap;
import java.util.Map;

// TranspositionTable.java
public class TranspositionTable {

    public enum Flag { EXACT, LOWER_BOUND, UPPER_BOUND }

    public static class TTEntry {
        public double score;
        public int depth;
        public Flag flag;

        public TTEntry(double score, int depth, Flag flag) {
            this.score = score;
            this.depth = depth;
            this.flag = flag;
        }
    }

    private final Map<Integer, TTEntry> table = new HashMap<>(); // ← int key

    public TTEntry get(int key) { return table.get(key); }

    public void put(int key, double score, int depth, Flag flag) {
        table.put(key, new TTEntry(score, depth, flag));
    }

    public void clear() { table.clear(); }
}