package dev.joao_guilherme.chess.board;

import dev.joao_guilherme.chess.enums.Color;
import dev.joao_guilherme.chess.pieces.*;

import java.util.*;
import java.util.stream.Collectors;

import static dev.joao_guilherme.chess.board.Position.*;
import static dev.joao_guilherme.chess.enums.Color.BLACK;
import static dev.joao_guilherme.chess.enums.Color.WHITE;

public class Board {

    private static Board instance;
    private Map<Color, Set<Piece>> pieces;
    private Color turn;

    private final Position[][] positions = {
            {A8, B8, C8, D8, E8, F8, G8, H8},
            {A7, B7, C7, D7, E7, F7, G7, H7},
            {A6, B6, C6, D6, E6, F6, G6, H6},
            {A5, B5, C5, D5, E5, F5, G5, H5},
            {A4, B4, C4, D4, E4, F4, G4, H4},
            {A3, B3, C3, D3, E3, F3, G3, H3},
            {A2, B2, C2, D2, E2, F2, G2, H2},
            {A1, B1, C1, D1, E1, F1, G1, H1}
    };

    private Board() {
        setupInitialPositions();
        turn = WHITE;
    }

    public static Board getInstance() {
        if (instance == null) {
            instance = new Board();
        }
        return instance;
    }

    private void setupInitialPositions() {
        pieces = Set.of(
                new Rook(WHITE, A1),
                new Knight(WHITE, B1),
                new Bishop(WHITE, C1),
                new Queen(WHITE, D1),
                new King(WHITE, E1),
                new Bishop(WHITE, F1),
                new Knight(WHITE, G1),
                new Rook(WHITE, H1),
                new Pawn(WHITE, A2),
                new Pawn(WHITE, B2),
                new Pawn(WHITE, C2),
                new Pawn(WHITE, D2),
                new Pawn(WHITE, E2),
                new Pawn(WHITE, F2),
                new Pawn(WHITE, G2),
                new Pawn(WHITE, H2),

                new Rook(BLACK, A8),
                new Knight(BLACK, B8),
                new Bishop(BLACK, C8),
                new Queen(BLACK, D8),
                new King(BLACK, E8),
                new Bishop(BLACK, F8),
                new Knight(BLACK, G8),
                new Rook(BLACK, H8),
                new Pawn(BLACK, A7),
                new Pawn(BLACK, B7),
                new Pawn(BLACK, C7),
                new Pawn(BLACK, D7),
                new Pawn(BLACK, E7),
                new Pawn(BLACK, F7),
                new Pawn(BLACK, G7),
                new Pawn(BLACK, H7)
        ).stream().collect(Collectors.groupingBy(Piece::getColor, Collectors.toSet()));
    }

    public boolean isKingInCheck(Color color) {
        King king = pieces.get(color).stream().filter(King.class::isInstance).map(King.class::cast).findFirst().orElseThrow(() -> new IllegalStateException("White king not found"));
        return pieces.get(color.opposite()).stream().anyMatch(piece -> piece.isValidMove(king.getPosition()));
    }

    public boolean isPiecePreventingCheck(Piece piece) {
        if (piece == null || !pieces.get(piece.getColor()).contains(piece)) return false;
        pieces.get(piece.getColor()).remove(piece);
        boolean kingInCheck = isKingInCheck(piece.getColor());
        pieces.get(piece.getColor()).add(piece);
        return kingInCheck;
    }

    public boolean isSafePositionForKing(King king, Position position) {
        return pieces.get(king.getColor().opposite()).stream().anyMatch(piece -> piece.isValidMove(position));
    }

    public Piece getPieceAt(Position position) {
        return findPieceAt(position).orElseThrow(() -> new IllegalArgumentException("No piece at " + position));
    }

    public Optional<Piece> findPieceAt(Position position) {
        return pieces.values().stream()
                .flatMap(Set::stream)
                .filter(piece -> piece.getPosition().equals(position))
                .findFirst();
    }

    public void capturePiece(Piece piece) {
        pieces.get(piece.getColor()).remove(piece);
    }

    public Set<Piece> getPieces() {
        return pieces.values().stream()
                .flatMap(Set::stream).collect(Collectors.toSet());
    }

    public List<Position> getPositions() {
        return Arrays.stream(positions).flatMap(Arrays::stream).toList();
    }

    public List<Position> getPositionsAvailableForPiece(Piece piece) {
        return getPositions().stream().filter(piece::isValidMove).toList();
    }

    public boolean movePiece(Position from, Position to) {
        Piece piece = getPieceAt(from).orElseThrow(() -> new IllegalArgumentException("No piece at " + from));
        if (piece.getColor() != turn) return false;
        if (piece instanceof King king && !isSafePositionForKing(king, to)) throw new IllegalArgumentException("Cannot move king to " + to + " as it would be in check");
        if (isKingInCheck(turn) && isPiecePreventingCheck(piece)) {
            throw new IllegalArgumentException("Cannot move piece as it would be in check");
        }
        getPieceAt(to).ifPresent(pieceAtDest -> {
            if (pieceAtDest.getColor() == piece.getColor()) {
                throw new IllegalArgumentException("Cannot capture your own piece at " + to);
            }
            capturePiece(pieceAtDest);
        });
        piece.moveTo(to);
        nextTurn();
        return true;
    }

    public Color getTurn() {
        return turn;
    }

    private void nextTurn() {
        turn = turn.opposite();
    }
}
