package dev.joao_guilherme.chess.movements;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.pieces.Piece;

public record MoveRecord(
        Position from,
        Position to,
        Piece movedPiece,
        Piece capturedPiece,
        Piece promotedFrom,
        Piece promotedTo,
        Position enPassantSquareBefore,
        Position enPassantSquareAfter,
        boolean castling,
        Position rookFrom,
        Position rookTo,
        int oldMoveCount
) {}

