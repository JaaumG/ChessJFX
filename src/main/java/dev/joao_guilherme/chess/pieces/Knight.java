package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import static dev.joao_guilherme.chess.board.Movement.isLShaped;
import static dev.joao_guilherme.chess.board.Movement.noSameColorPieceAtTarget;

public final class Knight extends Piece {

    public Knight(Color color, Position position) {
        super(color, "knight", position);
    }

    @Override
    public boolean isValidMove(Position newPosition) {
        return isLShaped(this.position, newPosition) && noSameColorPieceAtTarget(this.color, newPosition);
    }
}
