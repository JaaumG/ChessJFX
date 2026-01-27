package dev.joao_guilherme.chess.board;

import dev.joao_guilherme.chess.enums.Color;
import dev.joao_guilherme.chess.events.*;
import dev.joao_guilherme.chess.pieces.*;

import java.util.*;
import java.util.stream.Collectors;

import static dev.joao_guilherme.chess.board.Movement.*;
import static dev.joao_guilherme.chess.board.Position.*;
import static dev.joao_guilherme.chess.enums.Color.BLACK;
import static dev.joao_guilherme.chess.enums.Color.WHITE;


//TODO 25/01/2026: - Godclass necessita de refatoração, passando instância para Movement permitindo que o movimento das peças fique sobre controle total delas mesmas.
public class Board implements Cloneable {

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
    private Position enPassantAvailablePosition;
    private Map<Color, Set<Piece>> pieces;
    private Map<Position, Piece> pieceByPosition;
    private Color turn;
    private EventPublisher eventPublisher;

    public Board(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        setupInitialPositions();
        turn = WHITE;
    }

    private Board(Board board) {
        this.pieces = new HashMap<>();
        this.pieceByPosition = new HashMap<>();
        this.turn = board.turn;
        this.eventPublisher = new EventPublisher();
        for (Piece piece : board.getPieces()) {
            Piece clone = piece.clone();
            pieces.computeIfAbsent(clone.getColor(), k -> new HashSet<>()).add(clone);
            pieceByPosition.put(clone.getPosition(), clone);
        }
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
        pieceByPosition = pieces.values().stream().flatMap(Set::stream).collect(Collectors.toMap(Piece::getPosition, piece -> piece));
    }

    //TODO 25/01/2026: - Criar eventos para cheque e cheque-mate
    public boolean isCheckMate(Color color) {
        if (!isCheck(color)) return false;
        for (Piece piece : pieces.get(color)) {
            if (!piece.getPossibleMoves(this).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    //TODO 24/01/2026: - Identificar quando não for mais possivel fazer checkmate
    public boolean isStaleMate(Color color) {
        return !isCheck(color) && pieces.get(color).stream().allMatch(piece -> piece.getPossibleMoves(this).isEmpty());
    }

    private boolean promotion(Pawn pawn, Position from, Position to) {
        if (pawn.reachedLastRank(to)) {
            eventPublisher.publish(new PromotionRequestEvent(from, pawn, to));
            return  true;
        }
        return false;
    }

    public boolean promote(Position from, Position promotionPosition, Class<? extends Piece> tClass) {
        try {
            Piece promotedPiece = tClass.getConstructor(Color.class, Position.class).newInstance(turn, promotionPosition);
            pieces.get(promotedPiece.getColor()).add(promotedPiece);
            Pawn pawn = (Pawn) getPieceAt(from);
            pieces.get(pawn.getColor()).remove(pawn);
            eventPublisher.publish(new PromoteEvent(pawn, promotedPiece, promotionPosition));
            nextTurn();
            return true;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid piece class for promotion: " + tClass.getName(), e);
        }
    }

    public void movePieceAndPromote(Position from, Position to, Class<? extends Piece> promotionPieceClass) {
        Piece piece = findPieceAt(from).orElse(null);
        if (piece == null || piece.getColor() != turn) return;

        if (piece.moveTo(this, to)) {
            if (findPieceAt(to).isPresent() && findPieceAt(to).get() != piece) {
                pieces.get(turn.opposite()).remove(findPieceAt(to).get());
            }
            try {
                Piece promotedPiece = promotionPieceClass
                        .getConstructor(Color.class, Position.class)
                        .newInstance(piece.getColor(), to);

                pieces.get(piece.getColor()).remove(piece);
                pieces.get(piece.getColor()).add(promotedPiece);
                nextTurn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPieceMovementAvoidingCheck(Piece piece, Position to) {
        if (piece == null || !pieces.get(piece.getColor()).contains(piece) || !piece.isValidMove(this, to)) return false;
        Piece captured = pieceByPosition.get(to);
        Position from = piece.getPosition();

        pieceByPosition.remove(from);
        pieceByPosition.put(to, piece);
        piece.setPosition(to);
        if (captured != null) pieces.get(captured.getColor()).remove(captured);

        boolean safe = !findKing(piece.getColor()).isInCheck(this);

        piece.setPosition(from);
        pieceByPosition.put(from, piece);
        pieceByPosition.remove(to);
        if (captured != null) {
            pieceByPosition.put(to, captured);
            pieces.get(captured.getColor()).add(captured);
        }

        return safe;
    }

    public Piece getPieceAt(Position position) {
        return findPieceAt(position).orElseThrow(() -> new IllegalArgumentException("No piece at " + position));
    }

    public Optional<Piece> findPieceAt(Position position) {
        return pieceByPosition.containsKey(position) ? Optional.of(pieceByPosition.get(position)) : Optional.empty();
    }

    public boolean capturePiece(Piece piece, Piece capturedPiece) {
        Position from = piece.getPosition();
        if (capturedPiece == null || capturedPiece.getColor() == piece.getColor()) return false;
        if (capturedPiece instanceof King) return false;
        if (piece.moveTo(this, capturedPiece.getPosition()) && pieces.get(capturedPiece.getColor()).remove(capturedPiece)) {
            pieceByPosition.remove(capturedPiece.getPosition());
            pieceByPosition.put(piece.getPosition(), piece);
            eventPublisher.publish(new CaptureEvent(from, piece.getPosition(), piece, capturedPiece));
            nextTurn();
            return true;
        }
        return false;
    }

    public boolean capturePieceEnPassant(Pawn pawn, Piece capturedPiece, Position to) {
        if (pawn.moveTo(this, to) && pieces.get(capturedPiece.getColor()).remove(capturedPiece)) {
            pieceByPosition.remove(capturedPiece.getPosition());
            pieceByPosition.put(pawn.getPosition(), pawn);
            return true;
        }
        return false;
    }

    public Set<Piece> getPieces() {
        return pieces.values().stream()
                .flatMap(Set::stream).collect(Collectors.toSet());
    }

    public Set<Piece> getPieces(Color color) {
        return pieces.get(color);
    }

    public List<Position> getPositions() {
        return Arrays.stream(positions).flatMap(Arrays::stream).toList();
    }

    public boolean movePiece(Position from, Position to) {
        Piece piece = getPieceAt(from);
        if (piece.getColor() != turn || !isPieceMovementAvoidingCheck(piece, to)) return false;
        if (piece instanceof King king && isCastling(king, this, from, to)) return performCastlingMove(king, to);
        if (piece instanceof Pawn pawn) {
            if (isPawnTwoRowFirstMove(from, to)) enPassantAvailablePosition = Position.of(from.file(), (from.rank() + to.rank()) / 2);
            else if (isEnPassant(this, from, to, pawn.getColor())) return performEnPassantMove(pawn, from, to);
            else enPassantAvailablePosition = null;
            if (pawn.reachedLastRank(to)) return promotion(pawn, from, to);
        }
        if (isCapturingMove(this, piece, to)) return capturePiece(piece, getPieceAt(to));
        if (piece.moveTo(this, to)) {
            pieceByPosition.remove(from);
            pieceByPosition.put(to, piece);
            eventPublisher.publish(new MoveEvent(from, to, piece));
            nextTurn();
            return true;
        }
        return false;
    }

    private boolean performCastlingMove(King king, Position to) {
        return getRookForCastling(king, to)
                .map(rook -> {
                    Position kingStart = king.getPosition();
                    Position rookStart = rook.getPosition();
                    if (king.castle(this, to, rook)) {
                        pieceByPosition.remove(kingStart);
                        pieceByPosition.remove(rookStart);
                        pieceByPosition.put(king.getPosition(), king);
                        pieceByPosition.put(rook.getPosition(), rook);
                        eventPublisher.publish(new CastleEvent(king, rook, kingStart, rookStart));
                        nextTurn();
                        return true;
                    }
                    return false;
                }).orElse(false);
    }

    private boolean performEnPassantMove(Pawn pawn, Position from, Position to) {
        return getPawnForEnPassant(pawn, to)
                .filter(piece -> capturePieceEnPassant(pawn, piece, to))
                .map(piece -> {
                    eventPublisher.publish(new CaptureEvent(from, to, pawn, piece));
                    enPassantAvailablePosition = null;
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
                .filter(rook -> noPieceInBetween(this, king.getPosition(), rook.getPosition()));
    }

    public Color getTurn() {
        return turn;
    }

    private void nextTurn() {
        turn = turn.opposite();
        eventPublisher.publish(new TurnEvent(turn));
    }

    public boolean isEnPassantLocation(Position from) {
        return enPassantAvailablePosition != null && enPassantAvailablePosition.equals(from);
    }

    public King findKing(Color color) {
        return pieces.get(color).stream().filter(King.class::isInstance).map(King.class::cast).findFirst().orElseThrow(() -> new IllegalStateException(color + " king not found"));
    }

    public boolean isCheck(Color color) {
        return findKing(color).isInCheck(this);
    }

    @Override
    public Board clone() {
        return new Board(this);
    }
}
