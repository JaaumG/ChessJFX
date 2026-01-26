package dev.joao_guilherme.chess.events;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.pieces.Pawn;
import dev.joao_guilherme.chess.pieces.Piece;

public record PromoteEvent(Pawn pawn, Piece promotedPiece, Position promotedPosition) implements GameEvent {
}
