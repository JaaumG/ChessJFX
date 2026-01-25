package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import static dev.joao_guilherme.chess.board.Movement.*;

public final class Queen extends Piece {

    public Queen(Color color, Position position) {
        super(color, "queen", position);
    }

    @Override
    public boolean isValidMove(Position newPosition) {
        return isStraight(this.position, newPosition) || (isDiagonal(this.position, newPosition));
    }
}
