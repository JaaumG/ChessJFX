package dev.joao_guilherme.chess.board;

import dev.joao_guilherme.chess.enums.Color;


public abstract class Movement {

    private static boolean noPieceInBetweenDiagonal(Position from, Position to) {
        if (to == null || from == null || from.equals(to)) return false;
        int rowDirection = Integer.signum(to.getRow() - from.getRow());
        int columnDirection = Integer.signum(to.getColumn() - from.getColumn());
        int currentRow = from.getRow() + rowDirection;
        int currentColumn = from.getColumn() + columnDirection;

        while (currentRow != to.getRow() && currentColumn != to.getColumn()) {
            Position currentPosition = new Position(currentColumn, currentRow);
            if (Board.getInstance().getPieceAt(currentPosition).map(piece -> piece.isSameColor(Board.getInstance().getPieceAt(from).orElseThrow(IllegalStateException::new))).isPresent()) {
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
            if (Board.getInstance().getPieceAt(currentPosition).map(piece -> piece.isSameColor(Board.getInstance().getPieceAt(from).orElseThrow(IllegalStateException::new))).isPresent()) {
                return false;
            }
            currentRow += rowDirection;
            currentColumn += columnDirection;
        }
        return true;
    }

    private static boolean noSameColorPieceAtTarget(Color color, Position to) {
        return Board.getInstance().getPieceAt(to).map(piece -> piece.isNotSameColor(color)).orElse(true);
    }

    public static boolean noPieceAtTarget(Position to) {
        return Board.getInstance().findPieceAt(to).isEmpty();
    }

    public static boolean isDiagonal(Position from, Position to) {
        if (to == null || from == null || from.equals(to)) return false;
        return Math.abs(from.getColumn() - to.getColumn()) == Math.abs(from.getRow() - to.getRow()) && noPieceInBetweenDiagonal(from, to) && noSameColorPieceAtTarget(Board.getInstance().getPieceAt(from).orElseThrow(IllegalStateException::new).getColor(), to);
    }

    public static boolean isStraight(Position from, Position to) {
        if (to == null || from == null || from.equals(to)) return false;
        return (from.getColumn() == to.getColumn() || from.getRow() == to.getRow()) && noPieceInBetweenStraight(from, to) && noSameColorPieceAtTarget(Board.getInstance().getPieceAt(from).orElseThrow(IllegalStateException::new).getColor(), to);
    }

    public static boolean isLShaped(Position from, Position to) {
        if (to == null || from == null || from.equals(to)) return false;
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int columnDiff = Math.abs(from.getColumn() - to.getColumn());
        return (rowDiff == 2 && columnDiff == 1) || (rowDiff == 1 && columnDiff == 2) && noSameColorPieceAtTarget(Board.getInstance().getPieceAt(from).orElseThrow(IllegalStateException::new).getColor(), to);
    }

    public static boolean isUpward(Position from, Position to, Color color) {
        Board board = Board.getInstance();

        int direction = (color == Color.WHITE) ? 1 : -1;

        int fromRow = from.getRow();
        int toRow = to.getRow();
        int fromCol = from.getColumn();
        int toCol = to.getColumn();

        int rowDiff = toRow - fromRow;
        int colDiff = Math.abs(toCol - fromCol);

        var pieceAtTo = board.getPieceAt(to);

        // 1) Movimento simples (1 casa para frente, sem captura)
        if (colDiff == 0 && rowDiff == direction) {
            return pieceAtTo.isEmpty() && noPieceInBetweenStraight(from, to) && noSameColorPieceAtTarget(color, to);
        }

        // 2) Movimento duplo inicial (2 casas)
        if (colDiff == 0 && rowDiff == 2 * direction) {
            boolean isWhiteInitial = color == Color.WHITE && fromRow == 2;
            boolean isBlackInitial = color == Color.BLACK && fromRow == 7;
            boolean inInitialRank = isWhiteInitial || isBlackInitial;

            return inInitialRank
                    && pieceAtTo.isEmpty()
                    && noPieceInBetweenStraight(from, to)
                    && noSameColorPieceAtTarget(color, to);
        }

        // 3) Captura na diagonal (1 casa diagonal, peça adversária obrigatória)
        if (colDiff == 1 && rowDiff == direction) {
            return pieceAtTo
                    .map(targetPiece -> targetPiece.isNotSameColor(color))
                    .orElse(false);
        }

        return false;
    }

    public static int distance(Position from, Position to) {
        if (to == null || from == null || from.equals(to)) return 0;
        return Math.abs(from.getRow() - to.getRow()) + Math.abs(from.getColumn() - to.getColumn());
    }
}
