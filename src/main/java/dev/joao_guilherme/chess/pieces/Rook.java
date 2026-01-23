package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Movement;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

public final class Rook extends Piece {

    public Rook(Color color, Position position) {
        super(color, "rook", position);
    }

    @Override
    public boolean isValidMove(Position newPosition) {
        return Movement.isStraight(this.position, newPosition);
    }
}
