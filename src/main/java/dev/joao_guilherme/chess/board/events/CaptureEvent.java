package dev.joao_guilherme.chess.board.events;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.pieces.Piece;

public record CaptureEvent(Position position, Position capturedPosition, Piece piece, Piece capturedPiece) {

}
