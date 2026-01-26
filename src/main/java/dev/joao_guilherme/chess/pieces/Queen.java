package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import static dev.joao_guilherme.chess.board.Movement.*;

public final class Queen extends Piece {

    public Queen(Color color, Position position) {
        super(color, "queen", position);
    }

    private Queen(Queen piece) {
        this(piece.color, piece.position);
        this.moveCount = piece.moveCount;
    }

    @Override
    public boolean isValidMove(Board board, Position newPosition) {
        return (isStraight(this.position, newPosition) || (isDiagonal(this.position, newPosition)))
                && noPieceInBetween(board, position, newPosition) && noSameColorPieceAtTarget(board, color, newPosition);
    }

    @Override
    public int getValue() {
        return 9;
    }

    @Override
    public Piece clone() {
        return new Queen(this);
    }
}
