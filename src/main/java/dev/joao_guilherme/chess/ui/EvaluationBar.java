package dev.joao_guilherme.chess.ui;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.engine.BoardEvaluator;
import dev.joao_guilherme.chess.events.TurnEvent;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class EvaluationBar extends VBox {

    private final Rectangle whiteBar;
    private final Rectangle blackBackground;
    private final Label scoreLabel;
    private final Board board;

    public EvaluationBar() {
        this.board = BoardView.getInstance().getBoard();

        setMinWidth(30);
        setWidth(30);
        setMaxWidth(30);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-border-color: #555; -fx-border-width: 0 1 0 0; -fx-background-color: black;");

        StackPane barContainer = new StackPane();
        VBox.setVgrow(barContainer, Priority.ALWAYS);
        barContainer.setAlignment(Pos.BOTTOM_CENTER);

        blackBackground = new Rectangle();
        blackBackground.setFill(Color.BLACK);
        blackBackground.widthProperty().bind(this.widthProperty());
        blackBackground.heightProperty().bind(barContainer.heightProperty());

        whiteBar = new Rectangle();
        whiteBar.setFill(Color.WHITE);
        whiteBar.widthProperty().bind(this.widthProperty());

        barContainer.getChildren().addAll(blackBackground, whiteBar);

        scoreLabel = new Label("0.0");
        scoreLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2px;");
        scoreLabel.setMaxWidth(Double.MAX_VALUE);
        scoreLabel.setAlignment(Pos.CENTER);

        barContainer.heightProperty().addListener((obs, oldVal, newVal) -> updateEvaluation());

        StackPane overlay = new StackPane(barContainer, scoreLabel);
        VBox.setVgrow(overlay, Priority.ALWAYS);

        getChildren().add(overlay);

        setupListeners();
        Platform.runLater(this::updateEvaluation);
    }

    private void setupListeners() {
        board.getEventPublisher().subscribe(TurnEvent.class, e -> {
            Platform.runLater(this::updateEvaluation);
        });
    }

    public void updateEvaluation() {
        float score = BoardEvaluator.evaluate(board);

        scoreLabel.setText(String.format("%.1f", Math.abs(score / 10.0)));
        if (score >= 0) {
            scoreLabel.setStyle("-fx-text-fill: black; -fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 3;");
            StackPane.setAlignment(scoreLabel, Pos.BOTTOM_CENTER);
        } else {
            scoreLabel.setStyle("-fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.7); -fx-background-radius: 3;");
            StackPane.setAlignment(scoreLabel, Pos.TOP_CENTER);
        }


        double winPercentage = 1.0 / (1.0 + Math.exp(-score / 100.0));

        if (this.getParent() != null && this.getScene() != null) {
            double totalHeight = this.getHeight();
            if (totalHeight > 0) {
                whiteBar.setHeight(totalHeight * winPercentage);
            }
        }
    }
}
