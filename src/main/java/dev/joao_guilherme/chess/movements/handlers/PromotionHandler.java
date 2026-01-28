package dev.joao_guilherme.chess.movements.handlers;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.events.PromotionRequestEvent;
import dev.joao_guilherme.chess.movements.MoveHandler;
import dev.joao_guilherme.chess.movements.MoveRecordBuilder;
import dev.joao_guilherme.chess.movements.SupportsHistory;
import dev.joao_guilherme.chess.pieces.Pawn;
import dev.joao_guilherme.chess.pieces.Piece;

public class PromotionHandler implements MoveHandler, SupportsHistory {

    private MoveRecordBuilder record;

    @Override
    public void injectRecord(MoveRecordBuilder moveRecordBuilder) {
        this.record = moveRecordBuilder;
    }

    @Override
    public boolean canHandle(Piece piece, Position from, Position to, Board board) {
        return piece instanceof Pawn pawn && pawn.reachedLastRank(to);
    }

    @Override
    public boolean handle(Piece piece, Position from, Position to, Board board) {
        Pawn pawn = (Pawn) piece;

        if (!pawn.moveTo(board, to)) return false;

        board.updatePiecePosition(pawn, from, to);

        record.promotedFrom(pawn);

        board.getEventPublisher().publish(new PromotionRequestEvent(from, pawn, to));

        return true;
    }
}

