package dev.joao_guilherme.chess.movements;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.pieces.Piece;

import java.util.List;
import java.util.Objects;

public class MoveExecutor {

    private final Board board;
    private final List<MoveHandler> handlers;

    public MoveExecutor(Board board, List<MoveHandler> handlers) {
        this.board = board;
        this.handlers = handlers;
    }

    public boolean executeMove(Position from, Position to) {
        Piece piece = board.findPieceAt(from).orElse(null);
        if (piece == null) return false;

        MoveRecordBuilder recordBuilder = new MoveRecordBuilder(from, to, piece);
        recordBuilder.oldHalfMoveClock(board.getHalfMoveClock());
        Position epBefore = board.getEnPassantAvailablePosition();
        recordBuilder.enPassantBefore(epBefore);
        for (MoveHandler handler : handlers) {
            if (handler.canHandle(piece, from, to, board)) {
                if (handler instanceof SupportsHistory sh) sh.injectRecord(recordBuilder);
                boolean result = handler.handle(piece, from, to, board);
                if (result) {
                    if (Objects.equals(board.getEnPassantAvailablePosition(), epBefore)) board.clearEnPassant();
                    recordBuilder.enPassantAfter(board.getEnPassantAvailablePosition());
                    board.recordMove(recordBuilder.build());
                }
                return result;
            }
        }
        return false;
    }
}

