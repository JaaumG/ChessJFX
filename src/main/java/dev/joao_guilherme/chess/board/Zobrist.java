package dev.joao_guilherme.chess.board;

import dev.joao_guilherme.chess.enums.Color;
import dev.joao_guilherme.chess.pieces.*;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Zobrist {

    private static final long[][][] PIECES = new long[2][6][64];
    private static final long[] BLACK_TO_MOVE = new long[1];

    private static final Map<Class<? extends Piece>, Integer> PIECE_INDEX = new HashMap<>();

    static {
        SecureRandom random = new SecureRandom();

        PIECE_INDEX.put(Pawn.class, 0);
        PIECE_INDEX.put(Knight.class, 1);
        PIECE_INDEX.put(Bishop.class, 2);
        PIECE_INDEX.put(Rook.class, 3);
        PIECE_INDEX.put(Queen.class, 4);
        PIECE_INDEX.put(King.class, 5);

        for (int color = 0; color < 2; color++) {
            for (int piece = 0; piece < 6; piece++) {
                for (int sq = 0; sq < 64; sq++) {
                    PIECES[color][piece][sq] = random.nextLong();
                }
            }
        }
        BLACK_TO_MOVE[0] = random.nextLong();
    }

    private static int getPieceIndex(Piece piece) {
        return PIECE_INDEX.get(Objects.requireNonNull(piece).getClass());
    }

    public static long computeHash(Board board) {
        long hash = 0;
        if (board == null) return hash;
        for (Piece piece : board.getPieces()) {
            int color = piece.getColor() == Color.WHITE ? 0 : 1;
            int type = getPieceIndex(piece);
            int square = (piece.getPosition().getRow() - 1) * 8 + piece.getPosition().getColumn();

            hash ^= PIECES[color][type][square];
        }

        if (board.getTurn() == Color.BLACK) {
            hash ^= BLACK_TO_MOVE[0];
        }

        return hash;
    }
}