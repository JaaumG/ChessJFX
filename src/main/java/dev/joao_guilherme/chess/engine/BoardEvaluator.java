package dev.joao_guilherme.chess.engine;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.enums.Color;
import dev.joao_guilherme.chess.pieces.*;


import static dev.joao_guilherme.chess.engine.PositionEvaluator.evaluatePiecePosition;

public class BoardEvaluator {

    public static float evaluate(Board board) {
        float score = 0;
        for (Piece piece : board.getPieces()) {
            int materialValue = piece.getValue() * 10 * (piece.getColor() == Color.WHITE ? 1 : -1);
            score += evaluatePiecePosition(board, piece);
            score += materialValue;
        }
        return score;
    }
}
