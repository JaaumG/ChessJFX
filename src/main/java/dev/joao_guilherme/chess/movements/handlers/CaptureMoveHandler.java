package dev.joao_guilherme.chess.movements.handlers;


import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.events.CaptureEvent;
import dev.joao_guilherme.chess.movements.MoveHandler;
import dev.joao_guilherme.chess.movements.MoveRecordBuilder;
import dev.joao_guilherme.chess.movements.SupportsHistory;
import dev.joao_guilherme.chess.pieces.Piece;

public class CaptureMoveHandler implements MoveHandler, SupportsHistory {

    private MoveRecordBuilder record;

    @Override
    public boolean canHandle(Piece piece, Position from, Position to, Board board) {
        return board.findPieceAt(to).map(p -> p.getColor() != piece.getColor()).isPresent();
    }

    @Override
    public boolean handle(Piece piece, Position from, Position to, Board board) {
        Piece captured = board.findPieceAt(to).orElse(null);
        if (captured == null) return false;

        record.captured(captured);

        if (!piece.moveTo(board, to)) return false;

        board.capturePiece(captured);
        board.updatePiecePosition(piece, from, to);

        board.getEventPublisher().publish(new CaptureEvent(from, to, piece, captured));
        board.nextTurn();
        return true;
    }

    @Override
    public void injectRecord(MoveRecordBuilder moveRecordBuilder) {
        this.record = moveRecordBuilder;
    }
}
