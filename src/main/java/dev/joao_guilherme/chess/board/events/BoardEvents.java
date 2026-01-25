package dev.joao_guilherme.chess.board.events;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.pieces.King;
import dev.joao_guilherme.chess.pieces.Piece;

import java.util.function.Consumer;
import java.util.function.Function;

public class BoardEvents {

    private Consumer<CaptureEvent> capturePieceEvent;
    private Consumer<King> castlingEvent;
    private Function<Piece, Class<? extends Piece>> promotionEvent;
    private Consumer<MoveEvent> moveEvent;

    public void addPieceCapturedEvent(Consumer<CaptureEvent> event) {
        this.capturePieceEvent = event;
    }

    public void notifyPieceCaptured(Position position, Position capturedPosition, Piece piece, Piece capturedPiece) {
        capturePieceEvent.accept(new CaptureEvent(position, capturedPosition, piece, capturedPiece));
    }

    public void addPromotionEvent(Function<Piece, Class<? extends Piece>> event) {
        this.promotionEvent = event;
    }

    public Class<? extends Piece> notifyPiecePromoted(Piece piece) {
        return promotionEvent.apply(piece);
    }

    public void addCastlingEvent(Consumer<King> event) {
        this.castlingEvent = event;
    }

    public void notifyKingCastled(King king) {
        castlingEvent.accept(king);
    }

    public void addMoveEvent(Consumer<MoveEvent> event) {
        this.moveEvent = event;
    }

    public void notifyMove(Position from, Position to, Piece piece) {
        moveEvent.accept(new MoveEvent(from, to, piece));
    }
}
