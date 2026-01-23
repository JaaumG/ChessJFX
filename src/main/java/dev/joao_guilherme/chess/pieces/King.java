package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Movement;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

public final class King extends Piece {

    public King(Color color, Position position) {
        super(color, "king", position);
    }

    @Override
    public boolean isValidMove(Position newPosition) {
        return (Movement.isStraight(this.position, newPosition) || Movement.isDiagonal(this.position, newPosition)) && Movement.distance(this.position, newPosition) <= 1;
    }
}
