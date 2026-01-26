package dev.joao_guilherme.chess.events;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.pieces.King;
import dev.joao_guilherme.chess.pieces.Rook;

public record CastleEvent(King king, Rook rook, Position rookPreviousPosition, Position kingPreviousPosition) implements GameEvent {
}
