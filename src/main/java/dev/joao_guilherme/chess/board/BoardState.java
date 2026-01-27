package dev.joao_guilherme.chess.board;

import dev.joao_guilherme.chess.pieces.Piece;

record BoardState(
        Position from,
        Position to,
        Piece movedPiece,
        Piece capturedPiece,
        Position oldEnPassantAvailablePosition,
        int oldMoveCount,
        boolean wasPromotion,
        boolean wasCastling
) {}
