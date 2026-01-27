package dev.joao_guilherme.chess.movements;

import dev.joao_guilherme.chess.board.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoveLookups {

    private static final List<Position>[] KNIGHT_MOVES = new List[64];
    private static final List<Position>[] KING_MOVES = new List[64];
    private static final List<Position>[][] ROOK_RAYS = new List[64][4];
    private static final List<Position>[][] BISHOP_RAYS = new List[64][4];

    private static final int[][] KNIGHT_OFFSETS = {
            {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2},
            {1, 2}, {2, 1}, {2, -1}, {1, -2}
    };

    private static final int[][] KING_OFFSETS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
    };

    static {
        initializeLookups();
        initializeRays();
    }

    private static void initializeLookups() {
        for (int i = 0; i < 64; i++) {
            KNIGHT_MOVES[i] = computeMovesForSquare(i, KNIGHT_OFFSETS);
            KING_MOVES[i] = computeMovesForSquare(i, KING_OFFSETS);
        }
    }

    private static void initializeRays() {
        int[][] rookDirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        int[][] bishopDirs = {{1, 1}, {1, -1}, {-1, -1}, {-1, 1}};

        for (int i = 0; i < 64; i++) {
            ROOK_RAYS[i] = computeRays(i, rookDirs);
            BISHOP_RAYS[i] = computeRays(i, bishopDirs);
        }
    }

    private static List<Position>[] computeRays(int squareIndex, int[][] directions) {
        List<Position>[] rays = new List[directions.length];
        int startRow = squareIndex / 8;
        int startCol = squareIndex % 8;

        for (int d = 0; d < directions.length; d++) {
            List<Position> ray = new ArrayList<>();
            int col = startCol;
            int row = startRow;
            int dx = directions[d][0];
            int dy = directions[d][1];

            while (true) {
                col += dx;
                row += dy;
                if (!isValidBounds(col, row)) break;
                ray.add(Position.of(col, row + 1));
            }
            rays[d] = Collections.unmodifiableList(ray);
        }
        return rays;
    }

    private static List<Position> computeMovesForSquare(int squareIndex, int[][] offsets) {
        int row = squareIndex / 8;
        int col = squareIndex % 8;
        List<Position> moves = new ArrayList<>();

        for (int[] offset : offsets) {
            int targetCol = col + offset[0];
            int targetRow = row + offset[1];

            if (isValidBounds(targetCol, targetRow)) {
                moves.add(Position.of(targetCol, targetRow + 1));
            }
        }
        return Collections.unmodifiableList(moves);
    }

    public static List<Position>[] getRookRays(Position p) {
        return ROOK_RAYS[toIndex(p)];
    }

    public static List<Position>[] getBishopRays(Position p) {
        return BISHOP_RAYS[toIndex(p)];
    }

    private static boolean isValidBounds(int col, int row) {
        return col >= 0 && col < 8 && row >= 0 && row < 8;
    }

    public static List<Position> getKnightMoves(Position p) {
        return KNIGHT_MOVES[toIndex(p)];
    }

    public static List<Position> getKingMoves(Position p) {
        return KING_MOVES[toIndex(p)];
    }

    private static int toIndex(Position p) {
        return (p.getRow() - 1) * 8 + p.getColumn();
    }
}
