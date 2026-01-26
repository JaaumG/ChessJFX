package dev.joao_guilherme.chess.ui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundPlayer {

    public static void playMove() {
        new SoundPlayer().play("/sounds/move.mp3");
    }

    public static void playCapture() {
        new SoundPlayer().play("/sounds/capture.mp3");
    }

    public static void playCheck() {
        new SoundPlayer().play("/sounds/check.mp3");
    }

    public static void playCastling() {
        new SoundPlayer().play("/sounds/castle.mp3");
    }

    public static void playGameEnd() {
        new SoundPlayer().play("/sounds/game_end.mp3");
    }

    public static void playInvalidMove() {
        new SoundPlayer().play("/sounds/illegal.mp3");
    }

    private void play(String soundPath) {
        try {
            Media sound = new Media(getClass().getResource(soundPath).toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setOnReady(mediaPlayer::play);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
