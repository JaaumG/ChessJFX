package dev.joao_guilherme.chess.board.events;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.pieces.King;
import dev.joao_guilherme.chess.pieces.Pawn;
import dev.joao_guilherme.chess.pieces.Piece;
import dev.joao_guilherme.chess.pieces.Rook;

import java.util.function.Consumer;
import java.util.function.Function;

public class BoardEvents {

    private Consumer<CaptureEvent> capturePieceEvent;
    private Consumer<CastleEvent> castlingEvent;
    private Function<Piece, Class<? extends Piece>> promotionRequestEvent;
    private Consumer<MoveEvent> moveEvent;
    private Consumer<PromoteEvent> promoteEvent;

    public void addPieceCapturedEvent(Consumer<CaptureEvent> event) {
        this.capturePieceEvent = event;
    }

    public void notifyPieceCaptured(Position position, Position capturedPosition, Piece piece, Piece capturedPiece) {
        capturePieceEvent.accept(new CaptureEvent(position, capturedPosition, piece, capturedPiece));
    }

    public void addPromotionRequestEvent(Function<Piece, Class<? extends Piece>> event) {
        this.promotionRequestEvent = event;
    }

    public Class<? extends Piece> requestPiecePromoted(Piece piece) {
        return promotionRequestEvent.apply(piece);
    }

    public void addPromotionEvent(Consumer<PromoteEvent> event) {
        this.promoteEvent = event;
    }

    public void notifyPromotionEvent(Pawn pawn, Piece promotedPiece, Position previousPosition, Position promotedPosition) {
        promoteEvent.accept(new PromoteEvent(pawn, promotedPiece, previousPosition, promotedPosition));
    }

    public void addCastlingEvent(Consumer<CastleEvent> event) {
        this.castlingEvent = event;
    }

    public void notifyKingCastled(King king, Rook rook, Position rookPreviousPosition, Position kingPreviousPosition) {
        castlingEvent.accept(new CastleEvent(king, rook, rookPreviousPosition, kingPreviousPosition));
    }

    public void addMoveEvent(Consumer<MoveEvent> event) {
        this.moveEvent = event;
    }

    public void notifyMove(Position from, Position to, Piece piece) {
        moveEvent.accept(new MoveEvent(from, to, piece));
    }
}
