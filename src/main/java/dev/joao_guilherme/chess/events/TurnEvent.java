package dev.joao_guilherme.chess.events;

import dev.joao_guilherme.chess.enums.Color;
import dev.joao_guilherme.chess.movements.MoveRecord;

public record TurnEvent(Color color, MoveRecord lastMove) implements GameEvent {
}
