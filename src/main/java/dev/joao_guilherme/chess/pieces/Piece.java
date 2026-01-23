package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import java.util.Objects;

public abstract sealed class Piece permits Bishop, King, Knight, Queen, Pawn, Rook {

    final Color color;
    final String name;
    final String symbolPath;
    Position position;

    protected Piece(Color color, String name, Position position) {
        this.color = color;
        this.name = name;
        this.position = position;
        this.symbolPath = "/pieces/%s-%s.png".formatted(name, color.name().toLowerCase().charAt(0));
    }

    public abstract boolean isValidMove(Position newPosition);

    public boolean moveTo(Position newPosition) {
        if (isValidMove(newPosition)) {
            this.position = newPosition;
            return true;
        }
        return false;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getSymbolPath() {
        return symbolPath;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isSameColor(Piece piece) {
        return this.color == Objects.requireNonNull(piece).getColor();
    }

    public boolean isNotSameColor(Piece piece) {
        return !isSameColor(piece);
    }

    public boolean isNotSameColor(Color color) {
        return !this.color.equals(color);
    }

    @Override
    public String toString() {
        return "%s-%s".formatted(name, position);
    }
}
