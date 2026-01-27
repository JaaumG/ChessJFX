package dev.joao_guilherme.chess.events;

import dev.joao_guilherme.chess.pieces.King;

public record CheckEvent(King king) implements GameEvent {
}
