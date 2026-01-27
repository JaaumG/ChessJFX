package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.movements.MoveLookups;
import dev.joao_guilherme.chess.movements.Movement;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import java.util.ArrayList;
import java.util.List;

import static dev.joao_guilherme.chess.movements.Movement.*;

public final class King extends Piece {

    public King(Color color, Position position) {
        super(color, "king", position);
    }

    private King(King piece) {
        this(piece.color, piece.position);
        this.moveCount = piece.moveCount;
    }

    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> candidates = MoveLookups.getKingMoves(this.position);
        List<Position> validMoves = new ArrayList<>();

        for (Position target : candidates) {
            if (Movement.noSameColorPieceAtTarget(board, this.color, target) && board.isPieceMovementAvoidingCheck(this, target)) {
                validMoves.add(target);
            }
        }

        if (!hasMoved()) {
            addCastlingMoveIfValid(board, validMoves, 'g');
            addCastlingMoveIfValid(board, validMoves, 'c');
        }

        return validMoves;
    }

    private void addCastlingMoveIfValid(Board board, List<Position> moves, char fileChar) {
        Position target = Position.of(fileChar, this.position.rank());
        if (Movement.isCastling(this, board, this.position, target) && board.isPieceMovementAvoidingCheck(this, target)) {
            moves.add(target);
        }

    }

    @Override
    public boolean isValidMove(Board board, Position newPosition) {
        boolean basicMovement = ((isStraight(this.position, newPosition) && distance(this.position, newPosition) == 1) || (isDiagonal(this.position, newPosition) && distance(this.position, newPosition) == 2)) && noSameColorPieceAtTarget(board, color, newPosition);
        return  (basicMovement || (isCastling(this, board, this.position, newPosition) && !hasMoved()));
    }

    private boolean isValidMoveWithoutCastling(Board board, Position newPosition) {
        return ((isStraight(this.position, newPosition) && distance(this.position, newPosition) == 1) || (isDiagonal(this.position, newPosition) && distance(this.position, newPosition) == 2)) && noSameColorPieceAtTarget(board, color, newPosition);
    }

    @Override
    public int getValue() {
        return 10;
    }

    public boolean isInCheck(Board board) {
        return board.getPieces(this.color.opposite()).stream().anyMatch(piece -> piece instanceof King king ? king.isValidMoveWithoutCastling(board, this.position) : piece.isValidMove(board, this.position));
    }

    public boolean castle(Board board, Position newPosition, Rook rook) {
        boolean kingSide = newPosition.file() == 'g';
        Position rookTarget = Position.of((kingSide ? 'F' : 'D'), getPosition().rank());
        if (isCastling(this, board, this.position, newPosition) && !hasMoved()) {
            rook.moveTo(board, rookTarget);
            setPosition(newPosition);
            moveCount++;
            return true;
        }
        return false;
    }

    @Override
    public Piece clone() {
        return new King(this);
    }
}
