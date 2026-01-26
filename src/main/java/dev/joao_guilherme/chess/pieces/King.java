package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import static dev.joao_guilherme.chess.board.Movement.*;

public final class King extends Piece {

    public King(Color color, Position position) {
        super(color, "king", position);
    }

    private King(King piece) {
        this(piece.color, piece.position);
        this.moveCount = piece.moveCount;
    }

    @Override
    public boolean isValidMove(Board board, Position newPosition) {
        boolean basicMovement = ((isStraight(this.position, newPosition) && distance(this.position, newPosition) == 1) || (isDiagonal(this.position, newPosition) && distance(this.position, newPosition) == 2)) && noSameColorPieceAtTarget(board, color, newPosition);
        return  (basicMovement || (isCastling(this, board, this.position, newPosition) && !hasMoved()));
    }

    @Override
    public int getValue() {
        return 10;
    }

    public boolean isInCheck(Board board) {
        return board.getPieces(this.color.opposite()).stream().anyMatch(piece -> piece.isValidMove(board, this.position));
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
