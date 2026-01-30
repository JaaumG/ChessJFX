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
    private static final int MAX_Q_DEPTH = 4;
    private static final float MATE_SCORE = 1000000;
    private static final Class<? extends Piece>[] PROMOTION_PIECES = new Class[]{Queen.class, Rook.class, Bishop.class, Knight.class};
    private static final TranspositionTable TT = new TranspositionTable();

    public static Move computeMove(Board board) {
        return minimax(board, DEPTH, 0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    public static Move minimax(Board board, int depth, int ply, float alpha, float beta) {
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
        Move bestMove = new Move(null, null, isMaximizing ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY, null);

        List<Move> moves = generateAllMoves(board, false);

        if (moves.isEmpty()) return new Move(null, null, 0, null);

        for (Move moveObj : moves) {
            if (board.movePiece(moveObj.piece().getPosition(), moveObj.to())) {

                if (moveObj.promotion() != null) {
                    board.promote(moveObj.piece().getPosition(), moveObj.to(), moveObj.promotion());
                }

                Move result = minimax(board, depth - 1, ply + 1, alpha, beta);

                board.undo();

                Move currentMove = new Move(moveObj.piece(), moveObj.to(), result.eval(), moveObj.promotion());

                if (isMaximizing) {
                    if (result.eval() > bestMove.eval() || bestMove.piece() == null) bestMove = currentMove;
                    alpha = Math.max(alpha, result.eval());
                } else {
                    if (result.eval() < bestMove.eval() || bestMove.piece() == null) bestMove = currentMove;
                    beta = Math.min(beta, result.eval());
                }

                if (beta <= alpha) break;
            }
        }

        saveCache(zobristKey, bestMove, alphaOriginal, beta, depth);
        return bestMove;
    }

    private static float quiescenceSearch(Board board, float alpha, float beta, int qDepth) {
        return quiescenceSearchMinMax(board, alpha, beta, qDepth);
    }

    private static float quiescenceSearchMinMax(Board board, float alpha, float beta, int qDepth) {
        if (qDepth > MAX_Q_DEPTH) return BoardEvaluator.evaluate(board);

        float standPat = BoardEvaluator.evaluate(board);
        boolean isMaximizing = board.getTurn() == Color.WHITE;

        if (isMaximizing) {
            if (standPat >= beta) return beta;
            if (standPat > alpha) alpha = standPat;
        } else {
            if (standPat <= alpha) return alpha;
            if (standPat < beta) beta = standPat;
        }

        List<Move> captures = generateAllMoves(board, true);

        for (Move move : captures) {
            if (board.movePiece(move.piece().getPosition(), move.to())) {
                if (move.promotion() != null) board.promote(move.piece().getPosition(), move.to(), move.promotion());

                // Passa qDepth + 1
                float score = quiescenceSearchMinMax(board, alpha, beta, qDepth + 1);
                board.undo();

                if (isMaximizing) {
                    if (score >= beta) return beta;
                    if (score > alpha) alpha = score;
                } else {
                    if (score <= alpha) return alpha;
                    if (score < beta) beta = score;
                }
            }
        }
        return isMaximizing ? alpha : beta;
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
