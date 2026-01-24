package dev.joao_guilherme.chess.board;

import java.util.Objects;

public final class Position {

    public static final Position A1 = of('a', 1);
    public static final Position B1 = of('b', 1);
    public static final Position C1 = of('c', 1);
    public static final Position D1 = of('d', 1);
    public static final Position E1 = of('e', 1);
    public static final Position F1 = of('f', 1);
    public static final Position G1 = of('g', 1);
    public static final Position H1 = of('h', 1);
    public static final Position A2 = of('a', 2);
    public static final Position B2 = of('b', 2);
    public static final Position C2 = of('c', 2);
    public static final Position D2 = of('d', 2);
    public static final Position E2 = of('e', 2);
    public static final Position F2 = of('f', 2);
    public static final Position G2 = of('g', 2);
    public static final Position H2 = of('h', 2);
    public static final Position A3 = of('a', 3);
    public static final Position B3 = of('b', 3);
    public static final Position C3 = of('c', 3);
    public static final Position D3 = of('d', 3);
    public static final Position E3 = of('e', 3);
    public static final Position F3 = of('f', 3);
    public static final Position G3 = of('g', 3);
    public static final Position H3 = of('h', 3);
    public static final Position A4 = of('a', 4);
    public static final Position B4 = of('b', 4);
    public static final Position C4 = of('c', 4);
    public static final Position D4 = of('d', 4);
    public static final Position E4 = of('e', 4);
    public static final Position F4 = of('f', 4);
    public static final Position G4 = of('g', 4);
    public static final Position H4 = of('h', 4);
    public static final Position A5 = of('a', 5);
    public static final Position B5 = of('b', 5);
    public static final Position C5 = of('c', 5);
    public static final Position D5 = of('d', 5);
    public static final Position E5 = of('e', 5);
    public static final Position F5 = of('f', 5);
    public static final Position G5 = of('g', 5);
    public static final Position H5 = of('h', 5);
    public static final Position A6 = of('a', 6);
    public static final Position B6 = of('b', 6);
    public static final Position C6 = of('c', 6);
    public static final Position D6 = of('d', 6);
    public static final Position E6 = of('e', 6);
    public static final Position F6 = of('f', 6);
    public static final Position G6 = of('g', 6);
    public static final Position H6 = of('h', 6);
    public static final Position A7 = of('a', 7);
    public static final Position B7 = of('b', 7);
    public static final Position C7 = of('c', 7);
    public static final Position D7 = of('d', 7);
    public static final Position E7 = of('e', 7);
    public static final Position F7 = of('f', 7);
    public static final Position G7 = of('g', 7);
    public static final Position H7 = of('h', 7);
    public static final Position A8 = of('a', 8);
    public static final Position B8 = of('b', 8);
    public static final Position C8 = of('c', 8);
    public static final Position D8 = of('d', 8);
    public static final Position E8 = of('e', 8);
    public static final Position F8 = of('f', 8);
    public static final Position G8 = of('g', 8);
    public static final Position H8 = of('h', 8);
    private final char file;
    private final int rank;

    public Position(char file, int rank) {
        this.file = Character.toLowerCase(file);
        this.rank = rank;
    }

    public Position(int file, int rank) {
        this.file = (char) ('a' + file);
        this.rank = rank;
    }

    public static Position of(char file, int rank) {
        return new Position(file, rank);
    }

    public Position(String position) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        if (position.length() != 2) {
            throw new IllegalArgumentException("Invalid position format");
        }
        char file = position.toLowerCase().charAt(0);
        int rank = Character.getNumericValue(position.charAt(1));
        if (file < 'a' || file > 'h' || rank < 1 || rank > 8) {
            throw new IllegalArgumentException("Position out of bounds");
        }
        this(file, rank);
    }

    public static Position fromString(String sourcePosString) {
        return new Position(sourcePosString);
    }

    public int getRow() {
        return rank;
    }

    public int getColumn() {
        return file - 'a';
    }

    @Override
    public String toString() {
        return Character.toString(file).toLowerCase() + rank;
    }

    public char file() {
        return file;
    }

    public int rank() {
        return rank;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Position) obj;
        return this.file == that.file &&
                this.rank == that.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, rank);
    }
}
