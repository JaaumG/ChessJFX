package dev.joao_guilherme.chess.movements.handlers;


import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.events.CastleEvent;
import dev.joao_guilherme.chess.movements.MoveHandler;
import dev.joao_guilherme.chess.movements.MoveRecordBuilder;
import dev.joao_guilherme.chess.movements.SupportsHistory;
import dev.joao_guilherme.chess.pieces.King;
import dev.joao_guilherme.chess.pieces.Piece;
import dev.joao_guilherme.chess.pieces.Rook;

import java.util.Optional;

import static dev.joao_guilherme.chess.movements.Movement.isCastling;
import static dev.joao_guilherme.chess.movements.Movement.noPieceInBetween;

public class CastlingHandler implements MoveHandler, SupportsHistory {

    private MoveRecordBuilder record;

    @Override
    public boolean canHandle(Piece piece, Position from, Position to, Board board) {
        if (!(piece instanceof King king)) return false;
        return isCastling(king, board, from, to);
    }

    @Override
    public boolean handle(Piece piece, Position from, Position to, Board board) {
        King king = (King) piece;

        Optional<Rook> rookOpt = getRookForCastling(board, king, to);
        if (rookOpt.isEmpty()) return false;

        Rook rook = rookOpt.get();
        Position rookFrom = rook.getPosition();

        if (!king.castle(board, to, rook)) return false;

        record.castling(rookFrom, rook.getPosition());

        Position kingFinal = king.getPosition();
        Position rookFinal = rook.getPosition();

        board.updatePiecePosition(king, from, kingFinal);
        board.updatePiecePosition(rook, rookFrom, rookFinal);

        board.getEventPublisher().publish(new CastleEvent(king, rook, from, rookFrom));

        board.nextTurn();
        return true;
    }

    private Optional<Rook> getRookForCastling(Board board, King king, Position to) {
        boolean kingSide = to.file() == 'g';
        return board.findPieceAt(Position.of((kingSide ? 'H' : 'A'), king.getPosition().rank()))
                .filter(Rook.class::isInstance)
                .map(Rook.class::cast)
                .filter(rook -> rook.getColor() == king.getColor())
                .filter(rook -> noPieceInBetween(board, king.getPosition(), rook.getPosition()));
    }

    @Override
    public void injectRecord(MoveRecordBuilder moveRecordBuilder) {
        this.record = moveRecordBuilder;
    }
}
