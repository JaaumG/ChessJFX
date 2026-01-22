package dev.joao_guilherme.chess.enums;

public enum Color {
    BLACK, WHITE;

    public Color opposite() {
        return this == BLACK ? WHITE : BLACK;
    }
}
