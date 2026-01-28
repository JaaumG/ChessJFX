package dev.joao_guilherme.chess.ui;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.enums.Color;
import dev.joao_guilherme.chess.events.TurnEvent;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SidePanel extends VBox {

    private final Label turnLabel;

    public SidePanel() {
        setPadding(new Insets(35));
        setSpacing(30);
        setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-width: 0 0 0 1;");
        setAlignment(Pos.TOP_CENTER);
        setWidth(200);

        Label titleLabel = new Label("Vez de:");
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        turnLabel = new Label("WHITE");
        turnLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        updateTurnIndicator(Color.WHITE);
        Button flipButton = createButton("Inverter Tabuleiro");
        flipButton.setOnAction(e -> BoardView.getInstance().toggleRotation());
        Button undoButton = createButton("Desfazer (Undo)");
        undoButton.setOnAction(e -> BoardView.getInstance().undo());
        Button redoButton = createButton("Refazer (Redo)");
        redoButton.setOnAction(e -> BoardView.getInstance().redo());
        getChildren().addAll(titleLabel, turnLabel, createSpacer(20), flipButton, createSpacer(10), undoButton, redoButton);
        setupListeners();
    }

    private void setupListeners() {
        Board board = BoardView.getInstance().getBoard();

        board.getEventPublisher().subscribe(TurnEvent.class, event -> {
            Platform.runLater(() -> updateTurnIndicator(event.color()));
        });
    }

    private void updateTurnIndicator(Color color) {
        turnLabel.setText(color.name());
        if (color == Color.WHITE) {
            turnLabel.setStyle("-fx-text-fill: #333333;");
        } else {
            turnLabel.setStyle("-fx-text-fill: #000000; -fx-font-style: italic;");
        }
    }

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(10));
        btn.setStyle("-fx-font-size: 14px; -fx-cursor: hand;");
        return btn;
    }

    private Region createSpacer(double height) {
        Region spacer = new Region();
        spacer.setMinHeight(height);
        return spacer;
    }
}
