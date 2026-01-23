package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Movement;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

public final class Bishop extends Piece {

    public Bishop(Color color, Position position) {
        super(color, "bishop", position);
    }

    @Override
    public boolean isValidMove(Position newPosition) {
        return Movement.isDiagonal(this.position, newPosition);
    }
}
