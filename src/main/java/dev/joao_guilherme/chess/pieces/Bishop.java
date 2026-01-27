package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.MoveLookups;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import java.util.ArrayList;
import java.util.List;

import static dev.joao_guilherme.chess.board.Movement.*;

public final class Bishop extends Piece {

    public Bishop(Color color, Position position) {
        super(color, "bishop", position);
    }

    private Bishop(Bishop piece) {
        this(piece.color, piece.position);
        this.moveCount = piece.moveCount;
    }

    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> validMoves = new ArrayList<>();
        processRays(board, validMoves, MoveLookups.getBishopRays(this.position));
        return validMoves;
    }

    @Override
    public boolean isValidMove(Board board, Position newPosition) {
        return isDiagonal(this.position, newPosition) && noPieceInBetween(board, position, newPosition) && noSameColorPieceAtTarget(board, color, newPosition) ;
    }

    @Override
    public int getValue() {
        return 3;
    }

    @Override
    public Piece clone() {
        return new Bishop(this);
    }
}
