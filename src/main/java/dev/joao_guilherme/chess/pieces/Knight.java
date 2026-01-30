package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.movements.MoveLookups;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import java.util.ArrayList;
import java.util.List;

import static dev.joao_guilherme.chess.movements.Movement.isLShaped;
import static dev.joao_guilherme.chess.movements.Movement.noSameColorPieceAtTarget;

public final class Knight extends Piece {

    public Knight(Color color, Position position) {
        super(color, "knight", 3, position);
    }

    private Knight(Knight piece) {
        this(piece.color, piece.position);
        this.moveCount = piece.moveCount;
    }

    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> candidates = MoveLookups.getKnightMoves(this.position);

        List<Position> validMoves = new ArrayList<>();

        for (Position target : candidates) {
            if (noSameColorPieceAtTarget(board, this.color, target) && board.isPieceMovementAvoidingCheck(this, target)) {
                validMoves.add(target);
            }
        }
        return validMoves;
    }

    @Override
    public boolean isValidMove(Board board, Position newPosition) {
        return isLShaped(this.position, newPosition) && noSameColorPieceAtTarget(board, color, newPosition);
    }

    @Override
    public Piece clone() {
        return new Knight(this);
    }
}
