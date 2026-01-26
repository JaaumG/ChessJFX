package dev.joao_guilherme.chess.ui;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;
import dev.joao_guilherme.chess.pieces.*;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardView extends GridPane {

    private static BoardView instance;

    private final Map<Position, PositionView> squares = new HashMap<>();
    private final Board board;

    private BoardView() {
        board = new Board();
        for (Position position : board.getPositions()) {
            PositionView positionView = new PositionView(position);
            squares.put(position, positionView);
            int visualRow = 8 - position.getRow();
            add(positionView, position.getColumn(), visualRow);
        }

        refreshBoard();

        setWidth(8 * PositionView.TILE_SIZE);
        setHeight(8 * PositionView.TILE_SIZE);

        board.addPromotionRequestEvent(piece -> new PromotionPieceDialog(piece.getPosition()).showAndWait().orElse(null));
        board.addPieceCapturedEvent(event -> {
            squares.get(event.position()).getChildren().removeIf(PieceView.class::isInstance);
            squares.get(event.capturedPiece().getPosition()).getChildren().removeIf(PieceView.class::isInstance);
            squares.get(event.capturedPosition()).getChildren().add(new PieceView(event.piece()));
            SoundPlayer.playCapture();
        });
        board.addMoveEvent(event -> {
            squares.get(event.from()).getChildren().removeIf(PieceView.class::isInstance);
            squares.get(event.to()).getChildren().add(new PieceView(event.piece()));
            SoundPlayer.playMove();
        });
        board.addCastlingEvent(event -> {
            SoundPlayer.playCastling();
            squares.get(event.kingPreviousPosition()).getChildren().removeIf(PieceView.class::isInstance);
            squares.get(event.rookPreviousPosition()).getChildren().removeIf(PieceView.class::isInstance);
            squares.get(event.king().getPosition()).getChildren().add(new PieceView(event.king()));
            squares.get(event.rook().getPosition()).getChildren().add(new PieceView(event.rook()));
        });
        board.addPromotionEvent(event -> {
            squares.get(event.previousPosition()).getChildren().removeIf(PieceView.class::isInstance);
            squares.get(event.promotedPosition()).getChildren().removeIf(PieceView.class::isInstance);
            squares.get(event.promotedPosition()).getChildren().add(new PieceView(event.promotedPiece()));
        });
    }

    public static BoardView getInstance() {
        if (instance == null) {
            instance = new BoardView();
        }
        return instance;
    }

    public void showAvailablePositions(Piece piece, boolean show) {
        List<Position> positionsAvailableForPiece = piece.getPossibleMoves(board);
        squares.forEach((position, positionView) -> positionView.setHighlighted(positionsAvailableForPiece.contains(position) && show));
    }

    public void refreshBoard() {
        squares.forEach((_, positionView) -> positionView.getChildren().removeIf(PieceView.class::isInstance));
        board.getPieces().forEach(piece -> {
            PositionView stackPane = squares.get(piece.getPosition());
            stackPane.getChildren().add(new PieceView(piece));
        });
        rotateBoard();
    }

    private void rotateBoard() {
        int rotation = getTurn().equals(Color.WHITE) ? 0 : 180;
        setRotate(rotation);
        squares.forEach((_, positionView) -> positionView.setRotate(rotation));
    }

    public void performMove(Position origin, Position target) {
        if (!board.movePiece(origin, target)) {
            SoundPlayer.playInvalidMove();
        }
    }

    public Color getTurn() {
        return board.getTurn();
    }

    private void validateForCheckmate() {
        if (board.isCheckMate(getTurn())) {
            Position kingPosition = board.findKing(getTurn()).getPosition();
            squares.get(kingPosition).getChildren().stream().filter(PieceView.class::isInstance)
                    .map(PieceView.class::cast)
                    .forEach(kingView -> {
                        kingView.setStyle("-fx-effect: dropshadow(three-pass-box, red, 20, 0, 0, 0);");
                        kingView.setRotate(-90);
                    });
            System.out.println("Checkmate! " + getTurn().opposite() + " wins.");
        }
    }
}
