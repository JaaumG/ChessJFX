package dev.joao_guilherme.chess.movements;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.pieces.Piece;

public interface MoveHandler {

    boolean canHandle(Piece piece, Position from, Position to, Board board);
    boolean handle(Piece piece, Position from, Position to, Board board);
}
