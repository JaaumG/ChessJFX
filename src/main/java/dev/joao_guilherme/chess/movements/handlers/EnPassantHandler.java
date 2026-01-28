package dev.joao_guilherme.chess.movements.handlers;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.events.CaptureEvent;
import dev.joao_guilherme.chess.movements.MoveHandler;
import dev.joao_guilherme.chess.movements.MoveRecordBuilder;
import dev.joao_guilherme.chess.movements.SupportsHistory;
import dev.joao_guilherme.chess.pieces.Pawn;
import dev.joao_guilherme.chess.pieces.Piece;

import static dev.joao_guilherme.chess.movements.Movement.isEnPassant;

public class EnPassantHandler implements MoveHandler, SupportsHistory {

    private MoveRecordBuilder record;

    @Override
    public boolean canHandle(Piece piece, Position from, Position to, Board board) {
        if (!(piece instanceof Pawn pawn)) return false;
        return isEnPassant(board, from, to, pawn.getColor());
    }

    @Override
    public boolean handle(Piece piece, Position from, Position to, Board board) {
        Pawn pawn = (Pawn) piece;

        Position capturedPos = Position.of(to.file(), from.rank());
        Piece captured = board.findPieceAt(capturedPos).orElse(null);
        if (captured == null) return false;

        record.captured(captured);

        if (!pawn.moveTo(board, to)) return false;

        board.capturePiece(captured);
        board.updatePiecePosition(pawn, from, to);

        board.getEventPublisher().publish(new CaptureEvent(from, to, pawn, captured));

        board.clearEnPassant();
        board.nextTurn();
        return true;
    }

    @Override
    public void injectRecord(MoveRecordBuilder moveRecordBuilder) {
        this.record = moveRecordBuilder;
    }
}
