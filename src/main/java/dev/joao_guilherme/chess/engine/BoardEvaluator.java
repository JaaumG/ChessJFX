package dev.joao_guilherme.chess.engine;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.pieces.*;


import static dev.joao_guilherme.chess.engine.PositionEvaluator.evaluatePiecePosition;

public class BoardEvaluator {

    public static float evaluate(Board board) {
        float score = 0;
        for (Piece piece : board.getPieces()) {
            score += evaluatePiecePosition(board, piece);
            score += piece.getValue() * 100;
        }
        return score;
    }
}
