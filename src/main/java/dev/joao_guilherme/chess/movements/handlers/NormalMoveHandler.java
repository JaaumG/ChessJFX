package dev.joao_guilherme.chess.movements.handlers;


import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.events.MoveEvent;
import dev.joao_guilherme.chess.movements.MoveHandler;
import dev.joao_guilherme.chess.pieces.Pawn;
import dev.joao_guilherme.chess.pieces.Piece;

import static dev.joao_guilherme.chess.movements.Movement.isPawnTwoRowFirstMove;

public class NormalMoveHandler implements MoveHandler {

    @Override
    public boolean canHandle(Piece piece, Position from, Position to, Board board) {
        return board.findPieceAt(to).isEmpty() && !(piece instanceof Pawn pawn && pawn.reachedLastRank(to));
    }

    @Override
    public boolean handle(Piece piece, Position from, Position to, Board board) {
        if (!piece.moveTo(board, to)) return false;
        if (piece instanceof Pawn && isPawnTwoRowFirstMove(from, to)) board.setEnPassantPossible(from, to);
        else board.clearEnPassant();
        board.updatePiecePosition(piece, from, to);
        board.getEventPublisher().publish(new MoveEvent(from, to, piece));
        board.nextTurn();
        return true;
    }
}
