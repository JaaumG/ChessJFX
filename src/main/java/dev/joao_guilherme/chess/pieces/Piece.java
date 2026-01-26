package dev.joao_guilherme.chess.pieces;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;

import java.util.List;
import java.util.Objects;

public abstract sealed class Piece implements Cloneable permits Bishop, King, Knight, Queen, Pawn, Rook {

    final Color color;
    final String name;
    final String iconPath;
    Position position;
    int moveCount = 0;

    protected Piece(Color color, String name, Position position) {
        this.color = color;
        this.name = name;
        this.position = position;
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

    public List<Position> getPossibleMoves(Board board) {
        return board.getPositions().stream()
                .filter(to -> board.isPieceMovementAvoidingCheck(this, to))
                .toList();
    }

    public abstract int getValue();

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

    protected void setPosition(Position position) {
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

    @Override
    public Piece clone() {
        try {
            Piece clone = (Piece) super.clone();
            clone.moveCount = moveCount;
            clone.position = position;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
