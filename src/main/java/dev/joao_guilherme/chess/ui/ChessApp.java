package dev.joao_guilherme.chess.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChessApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        BoardView board = BoardView.getInstance();
        Scene scene = new Scene(board, board.getWidth(), board.getHeight());

        primaryStage.setTitle("Chess Game - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
