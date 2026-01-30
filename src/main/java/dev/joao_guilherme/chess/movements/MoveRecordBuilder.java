package dev.joao_guilherme.chess.movements;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.pieces.Piece;

public class MoveRecordBuilder {

    private final Position from;
    private final Position to;
    private final Piece movedPiece;

    private Piece capturedPiece;
    private Piece promotedFrom;
    private Piece promotedTo;

    private Position enPassantBefore;
    private Position enPassantAfter;

    private boolean castling;
    private Position rookFrom;
    private Position rookTo;

    private int oldMoveCount;
    private int oldHalfMoveClock;

    public MoveRecordBuilder(Position from, Position to, Piece movedPiece) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.oldMoveCount = movedPiece.getMoveCount();
    }

    public MoveRecordBuilder captured(Piece captured) {
        this.capturedPiece = captured;
        return this;
    }

    public MoveRecordBuilder promotedFrom(Piece piece) {
        this.promotedFrom = piece;
        return this;
    }

    public MoveRecordBuilder promotedTo(Piece piece) {
        this.promotedTo = piece;
        return this;
    }

    public MoveRecordBuilder enPassantBefore(Position pos) {
        this.enPassantBefore = pos;
        return this;
    }

    public MoveRecordBuilder enPassantAfter(Position pos) {
        this.enPassantAfter = pos;
        return this;
    }

    public MoveRecordBuilder castling(Position rookFrom, Position rookTo) {
        this.castling = true;
        this.rookFrom = rookFrom;
        this.rookTo = rookTo;
        return this;
    }

    public MoveRecordBuilder oldMoveCount(int count) {
        this.oldMoveCount = count;
        return this;
    }

    public MoveRecordBuilder oldHalfMoveClock(int clock) {
        this.oldHalfMoveClock = clock;
        return this;
    }

    public MoveRecord build() {
        return new MoveRecord(
                from,
                to,
                movedPiece,
                capturedPiece,
                promotedFrom,
                promotedTo,
                enPassantBefore,
                enPassantAfter,
                castling,
                rookFrom,
                rookTo,
                oldMoveCount,
                oldHalfMoveClock
        );
    }
}
