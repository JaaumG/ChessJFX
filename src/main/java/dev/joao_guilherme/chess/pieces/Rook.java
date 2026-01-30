package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.movements.MoveLookups;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import java.util.ArrayList;
import java.util.List;

import static dev.joao_guilherme.chess.movements.Movement.*;

public final class Rook extends Piece {

    public Rook(Color color, Position position) {
        super(color, "rook", 5, position);
    }

    private Rook(Rook piece) {
        this(piece.color, piece.position);
        this.moveCount = piece.moveCount;
    }

    @Override
    public boolean isValidMove(Board board, Position newPosition) {
        return isStraight(this.position, newPosition) && noPieceInBetween(board, position, newPosition) && noSameColorPieceAtTarget(board, color, newPosition);
    }

    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> validMoves = new ArrayList<>();

        processRays(board, validMoves, MoveLookups.getRookRays(this.position));

        return validMoves;
    }

    @Override
    public Piece clone() {
        return new Rook(this);
    }
}
