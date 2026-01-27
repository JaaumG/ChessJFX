package dev.joao_guilherme.chess.board;

import dev.joao_guilherme.chess.engine.Move;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {

    public static final int FLAG_EXACT = 0;
    public static final int FLAG_LOWERBOUND = 1; // Alpha (Falhou baixo)
    public static final int FLAG_UPPERBOUND = 2; // Beta (Falhou alto/Corte)

    public record TTEntry(long key, float score, int depth, int flag, Move bestMove) {}

    private final Map<Long, TTEntry> table = HashMap.newHashMap(100000);

    public void store(long key, float score, int depth, int flag, Move bestMove) {
        TTEntry existing = table.get(key);
        if (existing == null || depth >= existing.depth()) {
            table.put(key, new TTEntry(key, score, depth, flag, bestMove));
        }
    }

    public TTEntry probe(long key) {
        return table.get(key);
    }

    public void clear() {
        table.clear();
    }
}
