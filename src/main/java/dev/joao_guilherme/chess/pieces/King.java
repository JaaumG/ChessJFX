package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import static dev.joao_guilherme.chess.board.Movement.*;

public final class King extends Piece {

    public King(Color color, Position position) {
        super(color, "king", position);
    }

    @Override
    public boolean isValidMove(Position newPosition) {
        boolean basicMovement = (isStraight(this.position, newPosition) && distance(this.position, newPosition) == 1) || (isDiagonal(this.position, newPosition) && distance(this.position, newPosition) == 2);
        return basicMovement || (isCastling(this.position, newPosition) && !hasMoved());
    }
}
