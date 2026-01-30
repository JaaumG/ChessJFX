package dev.joao_guilherme.chess.engine;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.board.TranspositionTable;
import dev.joao_guilherme.chess.board.Zobrist;
import dev.joao_guilherme.chess.enums.Color;
import dev.joao_guilherme.chess.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.joao_guilherme.chess.movements.Movement.isCapturingMove;


public class ChessEngine {

    private static final int DEPTH = 4;
    private static final Class<? extends Piece>[] PROMOTION_PIECE = new Class[]{Queen.class, Rook.class, Bishop.class, Knight.class};
    private static final TranspositionTable TT = new TranspositionTable();

    public static Move computeMove(Board board) {
        return minimax(board, DEPTH, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    public static Move minimax(Board board, int depth, float alpha, float beta) {
        long zobristKey = Zobrist.computeHash(board);
        float alphaOriginal = alpha;
        Optional<Move> cache = getCache(zobristKey, depth, alpha, beta);
        if (cache.isPresent()) return cache.get();

        if (board.isCheckMate(Color.WHITE)) return new Move(null, null, -MATE_SCORE + ply, null);
        if (board.isCheckMate(Color.BLACK)) return new Move(null, null, MATE_SCORE - ply, null);
        if (board.isDraw()) return new Move(null, null, 0, null);

        if (depth == 0) {
            float score = quiescenceSearch(board, alpha, beta, 0);
            return new Move(null, null, score, null);
        }

        boolean isMaximizing = Color.WHITE == board.getTurn();

        Move best = new Move(null, null, isMaximizing ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY, null);
        for (Move moveObj : generateAllMoves(board)) {
            board.movePiece(moveObj.piece().getPosition(), moveObj.to());
            Move eval = minimax(board, depth - 1, alpha, beta);
            board.undo();
            Move candidateMove = new Move(moveObj.piece(), moveObj.to(), eval.eval(), moveObj.promotion());
            if (isMaximizing) {
                if (eval.eval() > best.eval() || best.piece() == null) best = candidateMove;
                alpha = Math.max(alpha, eval.eval());
            } else {
                if (eval.eval() < best.eval() || best.piece() == null) best = candidateMove;
                beta = Math.min(beta, eval.eval());
            }
            if (beta <= alpha) break;
        }

        saveCache(zobristKey, best, alphaOriginal, beta, depth);
        return best;
    }

    private static Optional<Move> getCache(long zobristKey, int depth, float alpha, float beta) {
        TranspositionTable.TTEntry ttEntry = TT.probe(zobristKey);

        if (ttEntry != null && ttEntry.depth() >= depth) {
            Move move = ttEntry.bestMove();
            if (ttEntry.flag() == TranspositionTable.FLAG_EXACT) {
                return Optional.of(move);
            } else if (ttEntry.flag() == TranspositionTable.FLAG_LOWERBOUND) {
                alpha = Math.max(alpha, ttEntry.score());
            } else if (ttEntry.flag() == TranspositionTable.FLAG_UPPERBOUND) {
                beta = Math.min(beta, ttEntry.score());
            }

            if (alpha >= beta) return Optional.of(move);
        }
        return Optional.empty();
    }

    private static void saveCache(long zobristKey, Move move, float alphaOriginal, float beta, int depth) {
        int flag;
        if (move.eval() <= alphaOriginal) {
            flag = TranspositionTable.FLAG_UPPERBOUND; // Fail-low
        } else if (move.eval() >= beta) {
            flag = TranspositionTable.FLAG_LOWERBOUND; // Fail-high
        } else {
            flag = TranspositionTable.FLAG_EXACT;
        }

        TT.store(zobristKey, move.eval(), depth, flag, move);
    }

    private static List<Move> generateAllMoves(Board board, boolean onlyCaptures) {
        List<Piece> pieces = new ArrayList<>(board.getPieces(board.getTurn()));
        List<Move> allMoves = new ArrayList<>();

        for (Piece piece : pieces) {
            for (Position move : piece.getPossibleMoves(board)) {
                boolean isCapture = isCapturingMove(board, piece, move);

                boolean isPromotion = (piece instanceof Pawn pawn && pawn.reachedLastRank(move));

                if (onlyCaptures && !isCapture && !isPromotion) continue;

                if (isPromotion) {
                    if (onlyCaptures) {
                        allMoves.add(new Move(piece, move, 0, Queen.class));
                    } else {
                        for (Class<? extends Piece> promotionType : PROMOTION_PIECES) {
                            allMoves.add(new Move(piece, move, 0, promotionType));
                        }
                    }
                } else {
                    allMoves.add(new Move(piece, move, 0, null));
                }
            }
        }

        allMoves.sort((m1, m2) -> {
            int score1 = scoreMove(board, m1.piece(), m1.to());
            int score2 = scoreMove(board, m2.piece(), m2.to());
            return Integer.compare(score2, score1);
        });

        return allMoves;
    }

    private static int scoreMove(Board board, Piece piece, Position to) {
        int score = 0;
        if (isCapturingMove(board, piece, to)) {
            Piece victim = board.getPieceAt(to);
            score += 10 * victim.getValue() - piece.getValue();
        }
        if (piece instanceof Pawn && board.isEnPassantLocation(piece.getColor(), to)) score += 10 - 1;
        if (piece instanceof Pawn pawn && pawn.reachedLastRank(to)) score += 900;
        return score;
    }
}
