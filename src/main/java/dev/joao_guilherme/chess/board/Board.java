package dev.joao_guilherme.chess.board;

import dev.joao_guilherme.chess.enums.Color;
import dev.joao_guilherme.chess.events.*;
import dev.joao_guilherme.chess.movements.MoveExecutor;
import dev.joao_guilherme.chess.movements.MoveRecord;
import dev.joao_guilherme.chess.movements.handlers.*;
import dev.joao_guilherme.chess.pieces.*;

import java.util.*;
import java.util.stream.Collectors;

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
    private final HistoryManager history;
    private final MoveExecutor moveExecutor;
    private final EventPublisher eventPublisher;
    private Position enPassantAvailablePosition;
    private Map<Color, Set<Piece>> pieces;
    private Map<Position, Piece> pieceByPosition;
    private Color turn;
    private int halfMoveClock = 0;
    private final Map<Long, Integer> positionHistory = new HashMap<>();

    public Board(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        setupInitialPositions();
        this.turn = WHITE;
        this.moveExecutor = new MoveExecutor(
                this,
                List.of(
                        new PromotionHandler(),
                        new CastlingHandler(),
                        new EnPassantHandler(),
                        new CaptureMoveHandler(),
                        new NormalMoveHandler()
                )
        );
        this.history = new HistoryManager();
        updatePositionHistory();
        this.eventPublisher.publish(new TurnEvent(turn, null));
    }

    private Board(Board board) {
        this.pieces = new HashMap<>();
        this.pieceByPosition = new HashMap<>();
        this.turn = board.turn;
        this.eventPublisher = new EventPublisher();
        this.enPassantAvailablePosition = board.enPassantAvailablePosition;
        this.halfMoveClock = board.halfMoveClock;
        this.positionHistory.putAll(board.positionHistory);

        for (Piece piece : board.getPieces()) {
            Piece clone = piece.clone();
            pieces.computeIfAbsent(clone.getColor(), k -> new HashSet<>()).add(clone);
            pieceByPosition.put(clone.getPosition(), clone);
        }

        this.moveExecutor = new MoveExecutor(
                this,
                List.of(
                        new PromotionHandler(),
                        new CastlingHandler(),
                        new EnPassantHandler(),
                        new CaptureMoveHandler(),
                        new NormalMoveHandler()
                )
        );
        this.history = new HistoryManager();
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

    public boolean promote(Position from, Position promotionPosition, Class<? extends Piece> tClass) {
        try {
            Piece promotedPiece = tClass.getConstructor(Color.class, Position.class).newInstance(turn, promotionPosition);
            Piece pieceAtTarget = getPieceAt(promotionPosition);
            if (!(pieceAtTarget instanceof Pawn)) {
                pieceAtTarget = getPieceAt(from);
            }
            Pawn pawn = (Pawn) pieceAtTarget;
            MoveRecord lastRecord = history.pop();
            if (lastRecord != null) {
                MoveRecord completedRecord = new MoveRecord(
                        lastRecord.from(), lastRecord.to(), lastRecord.movedPiece(),
                        lastRecord.capturedPiece(), lastRecord.promotedFrom(),
                        promotedPiece,
                        lastRecord.enPassantSquareBefore(), lastRecord.enPassantSquareAfter(),
                        lastRecord.castling(), lastRecord.rookFrom(), lastRecord.rookTo(),
                        lastRecord.oldMoveCount(),
                        lastRecord.oldHalfMoveClock()
                );
                history.push(completedRecord);
            }

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

        Optional<Piece> capturedOpt = findPieceAt(to);
        if (capturedOpt.isPresent() && capturedOpt.get() != piece) {
            Piece captured = capturedOpt.get();
            capturePiece(captured);
        }
        if (piece.moveTo(this, to)) {
            try {
                Piece promotedPiece = promotionPieceClass
                        .getConstructor(Color.class, Position.class)
                        .newInstance(piece.getColor(), to);

                pieces.get(piece.getColor()).remove(piece);
                pieceByPosition.remove(from);
                pieces.get(piece.getColor()).add(promotedPiece);
                pieceByPosition.put(to, promotedPiece);

                MoveRecord record = new MoveRecord(
                        from, to, piece,
                        capturedPiece,
                        piece,
                        promotedPiece,
                        epBefore,
                        null,
                        false,
                        null, null,
                        oldMoveCount,
                        oldHalfMoveClock
                );
                recordMove(record);

                nextTurn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPieceMovementAvoidingCheck(Piece piece, Position to) {
        if (piece == null || !pieces.get(piece.getColor()).contains(piece) || !piece.isValidMove(this, to)) return false;
        Optional<Piece> captured = findPieceAt(to);
        Position from = piece.getPosition();

        captured.ifPresent(this::capture);
        updatePiecePosition(piece, from, to);

        boolean safe = !findKing(piece.getColor()).isInCheck(this);

        updatePiecePosition(piece, to, from);
        captured.ifPresent(this::unCapture);

        return safe;
    }

    public Piece getPieceAt(Position position) {
        return findPieceAt(position).orElseThrow(() -> new IllegalArgumentException("No piece at " + position));
    }

    public Optional<Piece> findPieceAt(Position position) {
        return pieceByPosition.containsKey(position) ? Optional.of(pieceByPosition.get(position)) : Optional.empty();
    }

    public void capturePiece(Piece capturedPiece) {
        if (capturedPiece == null) return;
        if (capturedPiece.getColor() == turn) return;
        if (capturedPiece instanceof King) return;
        capture(capturedPiece);
    }

    public Set<Piece> getPieces() {
        return pieces.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
    }

    public Set<Piece> getPieces(Color color) {
        return pieces.get(color);
    }

    public List<Position> getPositions() {
        return Arrays.stream(positions).flatMap(Arrays::stream).toList();
    }

    private void capture(Piece captured) {
        pieces.get(captured.getColor()).remove(captured);
        pieceByPosition.remove(captured.getPosition());
    }

    private void unCapture(Piece captured) {
        pieces.get(captured.getColor()).add(captured);
        pieceByPosition.put(captured.getPosition(), captured);
    }

    public void updatePiecePosition(Piece piece, Position from, Position to) {
        pieceByPosition.remove(from);
        piece.setPosition(to);
        pieceByPosition.put(to, piece);
    }

    public boolean movePiece(Position from, Position to) {
        Piece piece = findPieceAt(from).orElse(null);
        if (piece == null) return false;
        if (piece.getColor() != turn) return false;
        if (!piece.isValidMove(this, to)) return false;
        if (!isPieceMovementAvoidingCheck(piece, to)) return false;
        return moveExecutor.executeMove(from, to);
    }

    public void redo() {
        MoveRecord rec = history.popRedo();
        if (rec == null) return;

        Piece piece = rec.movedPiece();

        this.turn = this.turn.opposite();

        this.enPassantAvailablePosition = rec.enPassantSquareBefore();

        if (rec.capturedPiece() != null) {
            removePiece(rec.capturedPiece());
        }

        updatePiecePosition(piece, rec.from(), rec.to());
        piece.incrementMoveCount();

        if (rec.promotedTo() != null) {
            Piece promoted = rec.promotedTo();
            removePiece(piece);
            addPiece(promoted);
            updatePiecePosition(promoted, rec.from(), rec.to());
        }

        this.enPassantAvailablePosition = rec.enPassantSquareAfter();

        if (rec.castling()) {
            Piece rook = getPieceAt(rec.rookFrom());
            updatePiecePosition(rook, rec.rookFrom(), rec.rookTo());
            rook.incrementMoveCount();
        }

        if (rec.movedPiece() instanceof Pawn || rec.capturedPiece() != null) {
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
        }

        history.push(rec);
        updatePositionHistory();
    }

    public void undo() {
        decrementPositionHistory();
        MoveRecord rec = history.pop();
        if (rec == null) return;

        this.turn = this.turn.opposite();

        this.enPassantAvailablePosition = rec.enPassantSquareBefore();

        Piece piece = rec.movedPiece();

        if (rec.promotedTo() != null) {
            Piece promoted = rec.promotedTo();
            removePiece(promoted);

            Piece original = rec.promotedFrom();
            addPiece(original);
            updatePiecePosition(original, rec.to(), rec.from());
            original.setMoveCount(rec.oldMoveCount());
        } else {
            updatePiecePosition(piece, rec.to(), rec.from());
            piece.setMoveCount(rec.oldMoveCount());
        }

        if (rec.capturedPiece() != null) {
            Piece captured = rec.capturedPiece();
            addPiece(captured);
            pieceByPosition.put(captured.getPosition(), captured);
        }

        if (rec.castling()) {
            Piece rook = getPieceAt(rec.rookTo());
            updatePiecePosition(rook, rec.rookTo(), rec.rookFrom());
            rook.setMoveCount(rook.getMoveCount() - 1);
        }

        this.halfMoveClock = rec.oldHalfMoveClock();
        history.pushRedo(rec);
    }

    private void updatePositionHistory() {
        long key = Zobrist.computeHash(this);
        positionHistory.merge(key, 1, Integer::sum);
    }

    private void decrementPositionHistory() {
        long key = Zobrist.computeHash(this);
        positionHistory.computeIfPresent(key, (k, v) -> v - 1);
    }

    public void addPiece(Piece piece) {
        pieces.get(piece.getColor()).add(piece);
        pieceByPosition.put(piece.getPosition(), piece);
    }

    public void removePiece(Piece piece) {
        pieces.get(piece.getColor()).remove(piece);
        pieceByPosition.remove(piece.getPosition());
    }

    public Color getTurn() {
        return turn;
    }

    public void nextTurn() {
        turn = turn.opposite();
        eventPublisher.publish(new TurnEvent(turn, history.peek()));
    }

    public boolean isEnPassantLocation(Color color, Position from) {
        return (enPassantAvailablePosition != null && enPassantAvailablePosition.equals(from)) && from.rank() == (color == WHITE ? 6 : 2);
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

    public EventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public void clearEnPassant() {
        this.enPassantAvailablePosition = null;
    }

    public void setEnPassantPossible(Position from, Position to) {
        this.enPassantAvailablePosition = Position.of(from.file(), (from.rank() + to.rank()) / 2);
    }

    public void recordMove(MoveRecord record) {
        if (record.movedPiece() instanceof Pawn || record.capturedPiece() != null) {
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
        }

        history.push(record);

        updatePositionHistory();
    }

    public Position getEnPassantAvailablePosition() {
        return enPassantAvailablePosition;
    }

    public HistoryManager getHistoryManager() {
        return history;
    }
}
