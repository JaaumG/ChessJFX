package dev.joao_guilherme.chess.board;

import dev.joao_guilherme.chess.enums.Color;
import dev.joao_guilherme.chess.pieces.*;

import java.util.*;
import java.util.stream.Collectors;

public class Board {

    private static Board instance;
    private Map<Color, Set<Piece>> pieces;
    private Color turn;

    private final Position[][] positions = {
            {Position.A8, Position.B8, Position.C8, Position.D8, Position.E8, Position.F8, Position.G8, Position.H8},
            {Position.A7, Position.B7, Position.C7, Position.D7, Position.E7, Position.F7, Position.G7, Position.H7},
            {Position.A6, Position.B6, Position.C6, Position.D6, Position.E6, Position.F6, Position.G6, Position.H6},
            {Position.A5, Position.B5, Position.C5, Position.D5, Position.E5, Position.F5, Position.G5, Position.H5},
            {Position.A4, Position.B4, Position.C4, Position.D4, Position.E4, Position.F4, Position.G4, Position.H4},
            {Position.A3, Position.B3, Position.C3, Position.D3, Position.E3, Position.F3, Position.G3, Position.H3},
            {Position.A2, Position.B2, Position.C2, Position.D2, Position.E2, Position.F2, Position.G2, Position.H2},
            {Position.A1, Position.B1, Position.C1, Position.D1, Position.E1, Position.F1, Position.G1, Position.H1}
    };

    private Board() {
        setupInitialPositions();
        turn = Color.WHITE;
    }

    public static Board getInstance() {
        if (instance == null) {
            instance = new Board();
        }
        return instance;
    }

    private void setupInitialPositions() {
        pieces = Set.of(
                new Rook(Color.WHITE, Position.A1),
                new Knight(Color.WHITE, Position.B1),
                new Bishop(Color.WHITE, Position.C1),
                new Queen(Color.WHITE, Position.D1),
                new King(Color.WHITE, Position.E1),
                new Bishop(Color.WHITE, Position.F1),
                new Knight(Color.WHITE, Position.G1),
                new Rook(Color.WHITE, Position.H1),
                new Pawn(Color.WHITE, Position.A2),
                new Pawn(Color.WHITE, Position.B2),
                new Pawn(Color.WHITE, Position.C2),
                new Pawn(Color.WHITE, Position.D2),
                new Pawn(Color.WHITE, Position.E2),
                new Pawn(Color.WHITE, Position.F2),
                new Pawn(Color.WHITE, Position.G2),
                new Pawn(Color.WHITE, Position.H2),

                new Rook(Color.BLACK, Position.A8),
                new Knight(Color.BLACK, Position.B8),
                new Bishop(Color.BLACK, Position.C8),
                new Queen(Color.BLACK, Position.D8),
                new King(Color.BLACK, Position.E8),
                new Bishop(Color.BLACK, Position.F8),
                new Knight(Color.BLACK, Position.G8),
                new Rook(Color.BLACK, Position.H8),
                new Pawn(Color.BLACK, Position.A7),
                new Pawn(Color.BLACK, Position.B7),
                new Pawn(Color.BLACK, Position.C7),
                new Pawn(Color.BLACK, Position.D7),
                new Pawn(Color.BLACK, Position.E7),
                new Pawn(Color.BLACK, Position.F7),
                new Pawn(Color.BLACK, Position.G7),
                new Pawn(Color.BLACK, Position.H7)
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

    public Optional<Piece> getPieceAt(Position position) {
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
