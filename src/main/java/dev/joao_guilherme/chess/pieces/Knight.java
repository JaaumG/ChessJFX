package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Movement;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

public final class Knight extends Piece {

    public Knight(Color color, Position position) {
        super(color, "knight", position);
    }

    @Override
    public boolean isValidMove(Position newPosition) {
        return Movement.isLShaped(this.position, newPosition);
    }
}
