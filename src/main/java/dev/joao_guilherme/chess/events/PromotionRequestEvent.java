package dev.joao_guilherme.chess.events;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.pieces.Pawn;

public record PromotionRequestEvent(
        Position from,
        Pawn pawn,
        Position position
) implements GameEvent {}

