package dev.joao_guilherme.chess.board.events;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.pieces.Piece;

public record MoveEvent(Position from, Position to, Piece piece) {

}
