package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import static dev.joao_guilherme.chess.board.Movement.isLShaped;
import static dev.joao_guilherme.chess.board.Movement.noSameColorPieceAtTarget;

public final class Knight extends Piece {

    public Knight(Color color, Position position) {
        super(color, "knight", position);
    }

    private Knight(Knight piece) {
        this(piece.color, piece.position);
        this.moveCount = piece.moveCount;
    }

    @Override
    public boolean isValidMove(Board board, Position newPosition) {
        return isLShaped(this.position, newPosition) && noSameColorPieceAtTarget(board, color, newPosition);
    }

    @Override
    public int getValue() {
        return 3;
    }

    @Override
    public Piece clone() {
        return new Knight(this);
    }
}
