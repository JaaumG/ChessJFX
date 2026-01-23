package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import static dev.joao_guilherme.chess.board.Movement.*;

public final class Pawn extends Piece {

    private static final int FIRST_MOVE_DISTANCE = 2;
    private static final int SECOND_MOVE_DISTANCE = 1;

    public Pawn(Color color, Position position) {
        super(color, "pawn", position);
    }

    @Override
    public boolean isValidMove(Position newPosition) {
        boolean basicsMovements = (isUpward(this.position, newPosition, this.color)
                && isOnSameColumn(this.position, newPosition)
                && distance(this.position, newPosition) <= (hasMoved() ? SECOND_MOVE_DISTANCE : FIRST_MOVE_DISTANCE)
                && noPieceAtTarget(newPosition));

        boolean diagonalCapture = (isDiagonal(this.position, newPosition) && distance(this.position, newPosition) == 2
                && isUpward(this.position, newPosition, this.color)
                && noSameColorPieceAtTarget(this.color, newPosition) && !noPieceAtTarget(newPosition));

        return basicsMovements || diagonalCapture || isEnPassant(this.position, newPosition, this.color);
    }
}
