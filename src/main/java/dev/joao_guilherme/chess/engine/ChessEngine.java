package dev.joao_guilherme.chess.engine;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;
import dev.joao_guilherme.chess.pieces.*;

import java.util.Set;


public class ChessEngine {

    private static final int DEPTH = 4;
    private static final Class<? extends Piece>[] PROMOTION_PIECE = new Class[]{Queen.class, Rook.class, Bishop.class, Knight.class};

    public static Move computeMove(Board board) {
        return newMinimax(board, DEPTH, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, board.getTurn().opposite());
    }

    public static Move newMinimax(Board board, int depth, float alpha, float beta, Color color) {
        if (depth == 0 || board.isCheckMate(board.getTurn())) {
            return new Move(null, null, BoardEvaluator.evaluate(board));
        }

        boolean isMaximizing = color == board.getTurn();
        Set<Piece> pieces = board.getPieces(board.getTurn());

        Move best = new Move(null, null, isMaximizing ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY);

        for (Piece piece : pieces) {
            for (Position move : piece.getPossibleMoves(board)) {

                Board clone = board.clone();
                if (piece instanceof Pawn pawn && pawn.reachedLastRank(move)) {
                    for (Class<? extends Piece> promotionType : PROMOTION_PIECE) {
                        clone.movePieceAndPromote(piece.getPosition(), move, promotionType);
                        Move eval = newMinimax(clone, depth - 1, alpha, beta, color);
                        if (isMaximizing) {
                            if (eval.eval() > best.eval()) best = new Move(piece, move, eval.eval());
                            alpha = Math.max(alpha, eval.eval());
                        } else {
                            if (eval.eval() < best.eval()) best = new Move(piece, move, eval.eval());
                            beta = Math.min(beta, eval.eval());
                        }
                        if (beta <= alpha) break;
                    }
                    continue;
                }
                Piece p = clone.getPieceAt(piece.getPosition());
                clone.movePiece(p.getPosition(), move);
                Move eval = newMinimax(clone, depth - 1, alpha, beta, color);
                System.out.println("Depth: " + (DEPTH - depth) + " Piece: " + piece.getName() + " From: " + piece.getPosition() + " To: " + move + " Eval: " + eval.eval());

                if (isMaximizing) {
                    if (eval.eval() > best.eval()) {
                        best = new Move(piece, move, eval.eval());
                    }
                    alpha = Math.max(alpha, eval.eval());
                } else {
                    if (eval.eval() < best.eval()) {
                        best = new Move(piece, move, eval.eval());
                    }
                    beta = Math.min(beta, eval.eval());
                }
                if (beta <= alpha) return best;
            }
        }
        return best;
    }
}
