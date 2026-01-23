package dev.joao_guilherme.chess.ui;

import dev.joao_guilherme.chess.pieces.Piece;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.util.Objects;

public class PieceView extends ImageView {

    private static final int PIECE_SIZE = 80;

    public PieceView(Piece piece) {
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(piece.getIconPath())));
            setUserData(piece);
            setImage(image);
            setFitWidth(PIECE_SIZE);
            setFitHeight(PIECE_SIZE);
            setPreserveRatio(true);
            setSmooth(true);
            setOnDragDetected(event -> {
                if (!BoardView.getInstance().getTurn().equals(piece.getColor())) {
                    event.consume();
                    return;
                }
                Dragboard db = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(piece.getPosition().toString());
                db.setContent(content);
                double offsetX = image.getWidth() / 2;
                double offsetY = image.getHeight() / 2;
                db.setDragView(image, offsetX, offsetY);
                BoardView.getInstance().showAvailablePositions(piece, true);
                event.consume();
            });
            setOnDragDone(event -> {
                BoardView.getInstance().showAvailablePositions(piece, false);
                event.consume();
            });
        } catch (NullPointerException _) {
            System.err.println("Imagem não encontrada: " + piece.getIconPath());
        }
    }
}