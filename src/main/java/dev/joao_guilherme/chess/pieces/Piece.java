package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import java.util.Objects;

public abstract sealed class Piece permits Bishop, King, Knight, Queen, Pawn, Rook {

    final Color color;
    final String name;
    final String iconPath;
    Position position;
    int moveCount = 0;
    final int value;

    protected Piece(Color color, String name, Position position, int value) {
        this.color = color;
        this.name = name;
        this.position = position;
        this.value = value;
        this.iconPath = "/pieces/%s-%s.png".formatted(name, color.name().toLowerCase().charAt(0));
    }

    public abstract boolean isValidMove(Board board, Position newPosition);

    public boolean moveTo(Board board, Position newPosition) {
        if (isValidMove(board, newPosition)) {
            setPosition(newPosition);
            moveCount++;
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

    public String getIconPath() {
        return iconPath;
    }

    public Position getPosition() {
        return position;
    }

    /**
     * For performing a move use the {@link Piece#moveTo(Board, Position)}
     *
     * this method is used internally to validation of moves and evaluation
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isSameColor(Piece piece) {
        return this.color == Objects.requireNonNull(piece).getColor();
    }

    public boolean isNotSameColor(Color color) {
        return !this.color.equals(color);
    }

    @Override
    public String toString() {
        return "%s-%s".formatted(name, position);
    }

    public boolean hasMoved() {
        return moveCount > 0;
    }
}
