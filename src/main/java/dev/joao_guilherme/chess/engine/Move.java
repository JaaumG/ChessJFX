package dev.joao_guilherme.chess.engine;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.pieces.Piece;

public record Move(Piece piece, Position to, float eval, Class<? extends Piece> promotion) {
}