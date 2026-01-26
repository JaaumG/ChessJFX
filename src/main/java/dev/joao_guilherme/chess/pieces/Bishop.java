package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import static dev.joao_guilherme.chess.board.Movement.*;

public final class Bishop extends Piece {

    public Bishop(Color color, Position position) {
        super(color, "bishop", position, 3);
    }

    @Override
    public boolean isValidMove(Board board, Position newPosition) {
        return isDiagonal(this.position, newPosition) && noPieceInBetween(board, position, newPosition) && noSameColorPieceAtTarget(board, color, newPosition) ;
    }
}
