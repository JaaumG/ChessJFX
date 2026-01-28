package dev.joao_guilherme.chess.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ChessApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        BoardView boardView = BoardView.getInstance();
        StackPane boardContainer = new StackPane(boardView);
        boardContainer.setStyle("-fx-background-color: #222;");
        SidePanel rightPanel = new SidePanel();
        EvaluationBar leftPanel = new EvaluationBar();
        root.setCenter(boardContainer);
        root.setRight(rightPanel);
        root.setLeft(leftPanel);
        Scene scene = new Scene(root, boardView.getWidth() + rightPanel.getWidth() + leftPanel.getWidth(), boardView.getHeight());
        primaryStage.setTitle("Chess Game - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
