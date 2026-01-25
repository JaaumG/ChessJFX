package dev.joao_guilherme.chess.ui;

import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;
import dev.joao_guilherme.chess.pieces.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class PromotionPieceDialog extends Dialog<Class<? extends Piece>> {

    public PromotionPieceDialog(Position position) {
        setTitle("Pawn Promotion");
        setHeaderText("Choose a piece to promote your pawn:");

        createIconButton("Queen", Queen.class, position);
        createIconButton("Rock", Rook.class, position);
        createIconButton("Bishop", Bishop.class, position);
        createIconButton("Knight", Knight.class, position);

        getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        setResultConverter(btn -> {
            if (btn == null || btn.getButtonData() != ButtonBar.ButtonData.OK_DONE) return null;
            return (Class<? extends Piece>) getDialogPane().lookupButton(btn).getUserData();
        });
    }

    private ButtonType createIconButton(String text, Class<? extends Piece> pieceClass, Position position) {
        try {
            ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(pieceClass.getConstructor(Color.class, Position.class).newInstance(position.rank() == 8 ? Color.WHITE : Color.BLACK, position).getIconPath()))));
            icon.setFitWidth(32);
            icon.setFitHeight(32);

            ButtonType btn = new ButtonType(text, ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().add(btn);
            Button btnNode = (Button) getDialogPane().lookupButton(btn);
            btnNode.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnNode.setGraphic(icon);
            btnNode.setUserData(pieceClass);
            return btn;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
