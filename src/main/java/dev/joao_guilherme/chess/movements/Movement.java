package dev.joao_guilherme.chess.movements;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;
import dev.joao_guilherme.chess.pieces.King;
import dev.joao_guilherme.chess.pieces.Piece;
import dev.joao_guilherme.chess.pieces.Rook;

import java.util.Optional;

import static java.lang.Math.abs;
import static java.util.function.Predicate.not;


public abstract class Movement {

    public static boolean noPieceInBetween(Board board, Position from, Position to) {
        return isDiagonal(from, to) ? noPieceInBetweenDiagonal(board,from, to) : noPieceInBetweenStraight(board, from, to);
    }

    private static boolean noPieceInBetweenDiagonal(Board board,Position from, Position to) {
        return isDiagonal(from, to) && noPieceInBetween(board, from, to, true);
    }

    private static boolean noPieceInBetweenStraight(Board board,Position from, Position to) {
        return isStraight(from, to) && noPieceInBetween(board, from, to, false);
    }

    private static boolean noPieceInBetween(Board board, Position from, Position to, boolean diagonal) {
        if (to == null || from == null || from.equals(to)) return false;
        int rowDir = Integer.signum(to.getRow() - from.getRow());
        int colDir = Integer.signum(to.getColumn() - from.getColumn());
        int currentRow = from.getRow() + rowDir;
        int currentCol = from.getColumn() + colDir;

        while (currentRow != to.getRow() || currentCol != to.getColumn()) {
            if (diagonal && Math.abs(currentRow - from.getRow()) != Math.abs(currentCol - from.getColumn())) break;
            Position pos = Position.of(currentCol, currentRow);
            Optional<Piece> blocker = board.findPieceAt(pos);
            if (blocker.isPresent()) return false;
            currentRow += rowDir;
            currentCol += colDir;
        }
        return true;
    }

    public static boolean isDiagonal(Position from, Position to) {
        if (isMovementInvalid(from, to)) return false;
        return Math.abs(from.getColumn() - to.getColumn()) == Math.abs(from.getRow() - to.getRow());
    }

    public static boolean isStraight(Position from, Position to) {
        if (isMovementInvalid(from, to)) return false;
        return (from.getColumn() == to.getColumn() || from.getRow() == to.getRow());
    }

    public static boolean isLShaped(Position from, Position to) {
        if (isMovementInvalid(from, to)) return false;
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int columnDiff = Math.abs(from.getColumn() - to.getColumn());
        return (rowDiff == 2 && columnDiff == 1) || (rowDiff == 1 && columnDiff == 2);
    }

    public static boolean isUpward(Position from, Position to, Color color) {
        if (isMovementInvalid(from, to)) return false;
        return (color == Color.WHITE ? to.getRow() > from.getRow() : to.getRow() < from.getRow());
    }

    public static boolean isOnSameColumn(Position from, Position to) {
        return from.getColumn() == to.getColumn();
    }

    public static int distance(Position from, Position to) {
        if (isMovementInvalid(from, to)) return 0;
        return Math.abs(from.getRow() - to.getRow()) + Math.abs(from.getColumn() - to.getColumn());
    }

    public static boolean isSideways(Position from, Position to) {
        if (isMovementInvalid(from, to)) return false;
        return (from.getRow() == to.getRow()) && (from.getColumn() != to.getColumn());
    }

    public static boolean isCastling(King king, Board board, Position from, Position to) {
        if (isMovementInvalid(from, to)) return false;
        if (!noPieceAtTarget(board, to)) return false;
        if (!isSideways(from, to)) return false;
        if (distance(from, to) != 2) return false;
        if (king.isInCheck(board)) return false;
        boolean kingSide = to.file() == 'g';
        return board.findPieceAt(Position.of((kingSide ? 'H' : 'A'), from.rank()))
                .filter(Rook.class::isInstance)
                .map(Rook.class::cast)
                .filter(rook -> rook.getColor() == king.getColor())
                .filter(rook -> noPieceInBetween(board, from, rook.getPosition()))
                .filter(not(Rook::hasMoved)).isPresent();
    }

    public static boolean isEnPassant(Board board, Position from, Position to, Color color) {
        if (isMovementInvalid(from, to)) return false;
        if (!isDiagonal(from, to)) return false;
        if (distance(from, to) != 2) return false;
        if (!noPieceAtTarget(board, to)) return false;
        if (!isUpward(from, to, color)) return false;
        return board.isEnPassantLocation(color, to);
    }

    public static boolean noPieceAtTarget(Board board, Position to) {
        return board.findPieceAt(to).isEmpty();
    }

    public static boolean noSameColorPieceAtTarget(Board board, Color color, Position to) {
        return board.findPieceAt(to).map(piece -> piece.isNotSameColor(color)).orElse(true);
    }

    public static boolean hasOpponentPieceAtTarget(Board board, Color color, Position position) {
        return board.findPieceAt(position).map(piece -> piece.isNotSameColor(color)).orElse(false);
    }

    public static boolean isCapturingMove(Board board, Piece piece, Position to) {
        return hasOpponentPieceAtTarget(board, piece.getColor(), to) && noSameColorPieceAtTarget(board, piece.getColor(), to) && !board.findPieceAt(to).map(King.class::isInstance).orElse(false);
    }

    public static boolean isPawnTwoRowFirstMove(Position from, Position to) {
        return abs(from.getRow() - to.getRow()) == 2;
    }

    private static boolean isMovementInvalid(Position from, Position to) {
        return (to == null || from == null || from.equals(to));
    }
}
