package dev.joao_guilherme.chess.board;

import dev.joao_guilherme.chess.enums.Color;


public abstract class Movement {

    public static boolean noPieceInBetween(Position from, Position to) {
        return isDiagonal(from, to) ? noPieceInBetweenDiagonal(from, to) : noPieceInBetweenStraight(from, to);
    }

    private static boolean noPieceInBetweenDiagonal(Position from, Position to) {
        if (to == null || from == null || from.equals(to)) return false;
        int rowDirection = Integer.signum(to.getRow() - from.getRow());
        int columnDirection = Integer.signum(to.getColumn() - from.getColumn());
        int currentRow = from.getRow() + rowDirection;
        int currentColumn = from.getColumn() + columnDirection;

        while (currentRow != to.getRow() && currentColumn != to.getColumn()) {
            Position currentPosition = new Position(currentColumn, currentRow);
            if (Board.getInstance().findPieceAt(currentPosition).map(piece -> piece.isSameColor(Board.getInstance().getPieceAt(from))).isPresent()) {
                return false;
            }
            currentRow += rowDirection;
            currentColumn += columnDirection;
        }
        return true;
    }

    private static boolean noPieceInBetweenStraight(Position from, Position to) {
        if (to == null || from == null || from.equals(to)) return false;
        int rowDirection = Integer.signum(to.getRow() - from.getRow());
        int columnDirection = Integer.signum(to.getColumn() - from.getColumn());
        int currentRow = from.getRow() + rowDirection;
        int currentColumn = from.getColumn() + columnDirection;
        while (currentRow != to.getRow() || currentColumn != to.getColumn()) {
            Position currentPosition = new Position(currentColumn, currentRow);
            if (Board.getInstance().findPieceAt(currentPosition).map(piece -> piece.isSameColor(Board.getInstance().getPieceAt(from))).isPresent()) {
                return false;
            }
            currentRow += rowDirection;
            currentColumn += columnDirection;
        }
        return true;
    }

    public static boolean noSameColorPieceAtTarget(Color color, Position to) {
        return Board.getInstance().findPieceAt(to).map(piece -> piece.isNotSameColor(color)).orElse(true);
    }

    public static boolean noPieceAtTarget(Position to) {
        return Board.getInstance().findPieceAt(to).isEmpty();
    }

    public static boolean isDiagonal(Position from, Position to) {
        if (to == null || from == null || from.equals(to)) return false;
        return Math.abs(from.getColumn() - to.getColumn()) == Math.abs(from.getRow() - to.getRow());
    }

    public static boolean isStraight(Position from, Position to) {
        if (to == null || from == null || from.equals(to)) return false;
        return (from.getColumn() == to.getColumn() || from.getRow() == to.getRow());
    }

    public static boolean isLShaped(Position from, Position to) {
        if (to == null || from == null || from.equals(to)) return false;
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int columnDiff = Math.abs(from.getColumn() - to.getColumn());
        return (rowDiff == 2 && columnDiff == 1) || (rowDiff == 1 && columnDiff == 2);
    }

    public static boolean isUpward(Position from, Position to, Color color) {
        if (to == null || from == null || from.equals(to)) return false;
        return (color == Color.WHITE ? to.getRow() > from.getRow() : to.getRow() < from.getRow()) && to.getColumn() == from.getColumn();
    }

    public static int distance(Position from, Position to) {
        if (to == null || from == null || from.equals(to)) return 0;
        return Math.abs(from.getRow() - to.getRow()) + Math.abs(from.getColumn() - to.getColumn());
    }

    public static boolean isSideways(Position from, Position to) {
        if (to == null || from == null || from.equals(to)) return false;
        return (from.getRow() == to.getRow()) && (from.getColumn() != to.getColumn());
    }

    public static boolean isCastling(Position from, Position to) {
        if (to == null || from == null || from.equals(to)) return false;
        if (!isSideways(from, to)) return false;
        if (distance(from, to) != 2) return false;
        return noPieceAtTarget(to);
    }

    public static boolean isEnPassant(Position from, Position to) {
        if (to == null || from == null || from.equals(to)) return false;
        if (!isDiagonal(from, to)) return false;
        if (distance(from, to) != 2) return false;
        return noPieceAtTarget(to);
    }
}
