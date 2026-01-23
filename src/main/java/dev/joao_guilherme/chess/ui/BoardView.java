package dev.joao_guilherme.chess.ui;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.pieces.Piece;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardView extends GridPane {

    private static BoardView instance;

    private final Map<Position, PositionView> squares = new HashMap<>();

    private BoardView() {
        for (Position position : Board.getInstance().getPositions()) {
            PositionView positionView = new PositionView(position);
            squares.put(position, positionView);
            int visualRow = 8 - position.getRow();
            add(positionView, position.getColumn(), visualRow);
        }

        refreshBoard();

        setWidth(8 * PositionView.TILE_SIZE);
        setHeight(8 * PositionView.TILE_SIZE);
    }

    public static BoardView getInstance() {
        if (instance == null) {
            instance = new BoardView();
        }
        return instance;
    }

    public void showAvailablePositions(Piece piece, boolean show) {
        List<Position> positionsAvailableForPiece = Board.getInstance().getPositionsAvailableForPiece(piece);
        squares.forEach((position, positionView) -> positionView.setHighlighted(positionsAvailableForPiece.contains(position) && show));
    }

    public void refreshBoard() {
        Board.getInstance().getPieces().forEach(piece -> {
            PositionView stackPane = squares.get(piece.getPosition());
            stackPane.getChildren().removeIf(PieceView.class::isInstance);
            stackPane.getChildren().add(new PieceView(piece));
        });
    }

    public void performMove(Position origin, Position target) {
        Board.getInstance().getPieceAt(origin).ifPresent(piece -> showAvailablePositions(piece, false));
        if (Board.getInstance().movePiece(origin, target)) {
            squares.get(origin).getChildren().removeIf(PieceView.class::isInstance);
            refreshBoard();
        }
    }
}
