package dev.joao_guilherme.chess.board;

import dev.joao_guilherme.chess.enums.Color;
import dev.joao_guilherme.chess.pieces.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static dev.joao_guilherme.chess.board.Movement.*;
import static dev.joao_guilherme.chess.board.Position.*;
import static dev.joao_guilherme.chess.enums.Color.BLACK;
import static dev.joao_guilherme.chess.enums.Color.WHITE;
import static java.lang.Math.abs;
import static java.util.function.Predicate.not;

public class Board {

    private Position enPassantAvailablePosition;
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
    private Map<Color, Set<Piece>> pieces;
    private Color turn;
    private Consumer<Piece> capturePieceEvent;
    private Consumer<King> castlingEvent;
    private Function<Piece, Class<? extends Piece>> promotionEvent;

    public Board() {
        setupInitialPositions();
        turn = WHITE;
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

    public boolean isCheckMate(Color color) {
        if (!isKingInCheck(color)) return false;
        for (Piece piece : pieces.get(color)) {
            if (!getPositionsAvailableForPiece(piece).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    //TODO 24/01/2026: - Identificar quando não for mais possivel fazer checkmate
    public boolean isStaleMate(Color color) {
        return !isKingInCheck(color) && pieces.get(color).stream().allMatch(piece -> getPositionsAvailableForPiece(piece).isEmpty());
    }

    public boolean promote(Pawn pawn, Position promotionPosition) {
        Class<? extends Piece> tClass = notifyPiecePromoted(pawn);
        if (tClass == null) return false;
        findPieceAt(promotionPosition).ifPresent(this::capturePiece);
        try {
            Piece promotedPiece = tClass.getConstructor(Color.class, Position.class).newInstance(pawn.getColor(), promotionPosition);
            pieces.get(promotedPiece.getColor()).add(promotedPiece);
            pieces.get(pawn.getColor()).remove(pawn);
            nextTurn();
            return true;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid piece class for promotion: " + tClass.getName(), e);
        }
    }

    public boolean isKingInCheck(Color color) {
        return pieces.get(color.opposite()).stream().anyMatch(piece -> piece.isValidMove(findKing(color).getPosition()));
    }

    public boolean isPieceMovementAvoidingCheck(Piece piece, Position to) {
        if (piece == null || !pieces.get(piece.getColor()).contains(piece) || !piece.isValidMove(to)) return false;
        Position original = piece.getPosition();

        Optional<Piece> target = piece instanceof Pawn pawn && isEnPassant(original, to, pawn.getColor()) ? getPawnForEnPassant(pawn, to).map(Piece.class::cast) : findPieceAt(to);
        target.ifPresent(t -> pieces.get(t.getColor()).remove(t));

        piece.setPosition(to);
        boolean kingInCheck = isKingInCheck(piece.getColor());

        piece.setPosition(original);
        target.ifPresent(t -> pieces.get(t.getColor()).add(t));

        return !kingInCheck;
    }

    public boolean isNotSafePositionForKing(King king, Position position) {
        return pieces.get(king.getColor().opposite()).stream().anyMatch(piece -> piece.isValidMove(position) && (piece instanceof Pawn pawn && pawn.getPosition().file() != position.file()));
    }

    public boolean isPawnTwoRowFirstMove(Position from, Position to) {
        return abs(from.getRow() - to.getRow()) == 2;
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
        if (pieces.get(piece.getColor()).remove(piece)) {
            notifyPieceCaptured(piece);
        }
    }

    public Set<Piece> getPieces() {
        return pieces.values().stream()
                .flatMap(Set::stream).collect(Collectors.toSet());
    }

    public List<Position> getPositions() {
        return Arrays.stream(positions).flatMap(Arrays::stream).toList();
    }

    public List<Position> getPositionsAvailableForPiece(Piece piece) {
        return getPositions().stream()
                .filter(piece::isValidMove)
                .filter(isValidMovementForKing(piece))
                .filter(isValidMovementForPawn(piece))
                .filter(to -> isMoveAllowed(piece, to))
                .toList();
    }

    private boolean isMoveAllowed(Piece piece, Position to) {
        return isPieceMovementAvoidingCheck(piece, to);
    }

    private Predicate<Position> isValidMovementForKing(Piece piece) {
        if (!(piece instanceof King king)) {
            return _ -> true;
        }
        return pos -> {
            if (isNotSafePositionForKing(king, pos)) return false;
            if (isCastling(king.getPosition(), pos)) return isCastlingAllowed(king, pos);
            return true;
        };
    }

    private Predicate<Position> isValidMovementForPawn(Piece piece) {
        if (!(piece instanceof Pawn pawn)) {
            return _ -> true;
        }
        return pos -> {
            if (isEnPassant(pawn.getPosition(), pos, pawn.getColor())) {
                return enPassantAvailablePosition != null
                        && enPassantAvailablePosition.equals(pos)
                        && getPawnForEnPassant(pawn, pos).isPresent();
            }
            return true;
        };
    }

    public boolean movePiece(Position from, Position to) {
        Piece piece = getPieceAt(from);
        if (piece.getColor() != turn) return false;
        Optional<Piece> target = findPieceAt(to);
        if (piece instanceof King king) {
            if (isNotSafePositionForKing(king, to)) return false;
            if (isCastling(from, to)) return performCastlingMove(king, to);
        }
        else if (!isMoveAllowed(piece, to)) return false;
        if (piece instanceof Pawn pawn) {
            if (isPawnTwoRowFirstMove(from, to)) enPassantAvailablePosition = Position.of(from.file(), (from.rank() + to.rank()) / 2);
            else if (isEnPassant(from, to, pawn.getColor())) return performEnPassantMove(pawn, to);
            else enPassantAvailablePosition = null;
            if (to.rank() == (pawn.getColor() == WHITE ? 8 : 1)) return promote(pawn, to);
        }
        if (piece.moveTo(to)) {
            target.ifPresent(this::capturePiece);
            nextTurn();
            return true;
        }
        return false;
    }

    private boolean performCastlingMove(King king, Position to) {
        boolean kingSide = to.file() == 'g';
        Position rookTarget = Position.of((kingSide ? 'F' : 'D'), king.getPosition().rank());
        return getRookForCastling(king, to)
                .filter(_ -> isCastlingAllowed(king, to))
                .map(rook -> {
                    rook.moveTo(rookTarget);
                    king.moveTo(to);
                    notifyKingCastled(king);
                    nextTurn();
                    return true;
                }).orElse(false);
    }

    private boolean performEnPassantMove(Pawn pawn, Position to) {
        return getPawnForEnPassant(pawn, to)
                .filter(_ -> isMoveAllowed(pawn, to))
                .map(piece -> {
                    capturePiece(piece);
                    pawn.moveTo(to);
                    nextTurn();
                    return true;
                }).orElse(false);
    }

    private Optional<Pawn> getPawnForEnPassant(Pawn pawn, Position to) {
        return findPieceAt(Position.of(to.file(), pawn.getPosition().rank()))
                .filter(Pawn.class::isInstance)
                .map(Pawn.class::cast)
                .filter(_ -> enPassantAvailablePosition != null && enPassantAvailablePosition.equals(to));
    }

    private Optional<Rook> getRookForCastling(King king, Position to) {
        boolean kingSide = to.file() == 'g';
        return findPieceAt(Position.of((kingSide ? 'H' : 'A'), king.getPosition().rank()))
                .filter(Rook.class::isInstance)
                .map(Rook.class::cast)
                .filter(rook -> rook.getColor() == king.getColor())
                .filter(rook -> noPieceInBetween(king.getPosition(), rook.getPosition()));
    }

    public Color getTurn() {
        return turn;
    }

    private boolean isCastlingAllowed(King king, Position to) {
        boolean kingSide = to.file() == 'g';
        Position rookTarget = Position.of((kingSide ? 'F' : 'D'), king.getPosition().rank());
        return getRookForCastling(king, to)
                .filter(not(Rook::hasMoved))
                .filter(_ -> isMoveAllowed(king, rookTarget)).isPresent();
    }

    private void nextTurn() {
        turn = turn.opposite();
    }

    private void printDebugState() {
        System.out.println("Turn: " + turn);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = positions[row][col];
                Optional<Piece> pieceOpt = findPieceAt(pos);
                if (pieceOpt.isPresent()) {
                    Piece piece = pieceOpt.get();
                    System.out.print(piece.getName().charAt(0));
                    System.out.print(piece.getColor() == WHITE ? "W " : "B ");
                } else {
                    System.out.print("-- ");
                }
            }
            System.out.println();
        }
    }

    public boolean noPieceInBetween(Position from, Position to) {
        return isDiagonal(from, to) ? noPieceInBetweenDiagonal(from, to) : noPieceInBetweenStraight(from, to);
    }

    private boolean noPieceInBetweenDiagonal(Position from, Position to) {
        return isDiagonal(from, to) && noPieceInBetween(from, to, true);
    }

    private boolean noPieceInBetweenStraight(Position from, Position to) {
        return isStraight(from, to) && noPieceInBetween(from, to, false);
    }

    private boolean noPieceInBetween(Position from, Position to, boolean diagonal) {
        if (to == null || from == null || from.equals(to)) return false;
        int rowDir = Integer.signum(to.getRow() - from.getRow());
        int colDir = Integer.signum(to.getColumn() - from.getColumn());
        int currentRow = from.getRow() + rowDir;
        int currentCol = from.getColumn() + colDir;

        while (currentRow != to.getRow() || currentCol != to.getColumn()) {
            if (diagonal && Math.abs(currentRow - from.getRow()) != Math.abs(currentCol - from.getColumn())) break;
            Position pos = new Position(currentCol, currentRow);
            boolean hasPieceSameColor = findPieceAt(pos)
                    .map(piece -> piece.isSameColor(getPieceAt(from)))
                    .isPresent();
            if (hasPieceSameColor) return false;
            currentRow += rowDir;
            currentCol += colDir;
        }
        return true;
    }

    public boolean noSameColorPieceAtTarget(Color color, Position to) {
        return findPieceAt(to).map(piece -> piece.isNotSameColor(color)).orElse(true);
    }

    public boolean noPieceAtTarget(Position to) {
        return findPieceAt(to).isEmpty();
    }

    public King findKing(Color color) {
        return pieces.get(color).stream().filter(King.class::isInstance).map(King.class::cast).findFirst().orElseThrow(() -> new IllegalStateException(color + " king not found"));
    }

    public void addPieceCapturedEvent(Consumer<Piece> event) {
        this.capturePieceEvent = event;
    }

    public void notifyPieceCaptured(Piece piece) {
        capturePieceEvent.accept(piece);
    }

    public void addPromotionEvent(Function<Piece, Class<? extends Piece>> event) {
        this.promotionEvent = event;
    }

    public Class<? extends Piece> notifyPiecePromoted(Piece piece) {
        return promotionEvent.apply(piece);
    }

    public void addCastlingEvent(Consumer<King> event) {
        this.castlingEvent = event;
    }

    public void notifyKingCastled(King king) {
        castlingEvent.accept(king);
    }
}
