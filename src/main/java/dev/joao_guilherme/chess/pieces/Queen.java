package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Movement;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

public final class Queen extends Piece {

    public Queen(Color color, Position position) {
        super(color, "queen", position);
    }

    @Override
    public boolean isValidMove(Position newPosition) {
        return Movement.isStraight(this.position, newPosition) || Movement.isDiagonal(this.position, newPosition);
    }
}
