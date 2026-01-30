package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import static dev.joao_guilherme.chess.movements.Movement.*;

public final class Pawn extends Piece {

    private static final int FIRST_MOVE_DISTANCE = 2;
    private static final int SECOND_MOVE_DISTANCE = 1;

    public Pawn(Color color, Position position) {
        super(color, "pawn", 1, position);
    }

    private Pawn(Pawn piece) {
        this(piece.color, piece.position);
        this.moveCount = piece.moveCount;
    }

    @Override
    public boolean isValidMove(Board board, Position newPosition) {
        boolean basicsMovements = isOnSameColumn(this.position, newPosition)
                && distance(this.position, newPosition) <= (hasMoved() ? SECOND_MOVE_DISTANCE : FIRST_MOVE_DISTANCE)
                && noPieceInBetween(board, this.position, newPosition)
                && noPieceAtTarget(board, newPosition);

        boolean diagonalCapture = isDiagonal(this.position, newPosition)
                && distance(this.position, newPosition) == 2
                && hasOpponentPieceAtTarget(board, this.color, newPosition);

        boolean enPassant = isEnPassant(board, this.position, newPosition, this.color);

        return noSameColorPieceAtTarget(board, this.color, newPosition) && isUpward(this.position, newPosition, this.color) && (basicsMovements || diagonalCapture || enPassant);
    }

    public boolean reachedLastRank(Position to) {
        return to.getRow() == (color.equals(Color.WHITE) ? 8 : 1);
    }

    @Override
    public Piece clone() {
        return new Pawn(this);
    }
}
