package dev.joao_guilherme.chess.ui;

import dev.joao_guilherme.chess.board.Position;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class PositionView extends StackPane {

    public static final int TILE_SIZE = 80;
    private static final String COR_CLARO = "#32674a";
    private static final String COR_ESCURO = "#e8e7e4";

    public PositionView(Position position) {
        Rectangle rectangle = new Rectangle(TILE_SIZE, TILE_SIZE);
        rectangle.setUserData(position);
        rectangle.setFill(Paint.valueOf((position.getRow() + position.getColumn()) % 2 == 0 ? COR_ESCURO : COR_CLARO));
        this.getChildren().add(rectangle);
        this.setOnDragOver(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        this.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String sourcePosString = db.getString();
                BoardView.getInstance().performMove(Position.fromString(sourcePosString), position);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
        this.setHighlighted(false);
    }

    public void setHighlighted(boolean highlighted) {
        Circle circle = new Circle();
        circle.setRadius(TILE_SIZE / 6.0);
        circle.setFill(Paint.valueOf("#ff0000"));
        circle.setCenterX(TILE_SIZE / 2.0);
        circle.setCenterY(TILE_SIZE / 2.0);
        circle.setOpacity(0.5);
        if (highlighted)
            this.getChildren().add(circle);
        else
            this.getChildren().removeIf(Circle.class::isInstance);
    }
}
