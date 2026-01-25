package dev.joao_guilherme.chess.board;

import dev.joao_guilherme.chess.enums.Color;


public abstract class Movement {

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

    public static boolean isCastling(Position from, Position to) {
        if (isMovementInvalid(from, to)) return false;
        if (!isSideways(from, to)) return false;
        return distance(from, to) == 2;
    }

    public static boolean isEnPassant(Position from, Position to, Color color) {
        if (isMovementInvalid(from, to)) return false;
        if (!isDiagonal(from, to)) return false;
        if (distance(from, to) != 2) return false;
        return isUpward(from, to, color);
    }

    private static boolean isMovementInvalid(Position from, Position to) {
        return (to == null || from == null || from.equals(to));
    }
}
