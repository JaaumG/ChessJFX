package dev.joao_guilherme.chess.events;

import dev.joao_guilherme.chess.enums.Color;

public record TurnEvent(Color color) implements GameEvent {
}
