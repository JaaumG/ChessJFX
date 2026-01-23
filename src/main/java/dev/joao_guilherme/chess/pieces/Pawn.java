package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import static dev.joao_guilherme.chess.board.Movement.*;

public final class Pawn extends Piece {

    private static final int FIRST_MOVE_DISTANCE = 2;
    private static final int SECOND_MOVE_DISTANCE = 1;
    private final Position startPosition;

    public Pawn(Color color, Position position) {
        super(color, "pawn", position);
        this.startPosition = position;
    }

    @Override
    public boolean isValidMove(Position newPosition) {
        return (isUpward(this.position, newPosition, this.color)
               && distance(this.position, newPosition) <= (isFirstMove() ? FIRST_MOVE_DISTANCE : SECOND_MOVE_DISTANCE)
               && noPieceAtTarget(newPosition))
               || (isDiagonal(this.position, newPosition) && distance(this.position, newPosition) == 2
                && noSameColorPieceAtTarget(this.color, newPosition) && !noPieceAtTarget(newPosition));
    }

    private boolean isFirstMove() {
        return startPosition.equals(this.position);
    }
}
