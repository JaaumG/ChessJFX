package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import static dev.joao_guilherme.chess.board.Movement.*;

public final class Rook extends Piece {

    public Rook(Color color, Position position) {
        super(color, "rook", position);
    }

    @Override
    public boolean isValidMove(Position newPosition) {
        return isStraight(this.position, newPosition) && noPieceInBetween(this.position, newPosition) && noSameColorPieceAtTarget(this.color, newPosition);
    }
}
