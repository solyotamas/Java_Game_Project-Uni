package classes.entities.additions;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class MusicPlayer {
    private static MediaPlayer mediaPlayer;

    public static void init(String resourcePath) {
        if (mediaPlayer == null) {
            URL url = MusicPlayer.class.getResource(resourcePath);
            if (url == null) throw new IllegalArgumentException("Nem található: " + resourcePath);
            mediaPlayer = new MediaPlayer(new Media(url.toExternalForm()));
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0.3);
        }
    }

    public static void play() {
        if (mediaPlayer != null) mediaPlayer.play();
    }

    public static void setVolume(double v) {
        if (mediaPlayer != null) mediaPlayer.setVolume(v);
    }

    public static double getVolume() {
        return mediaPlayer != null ? mediaPlayer.getVolume() : 0;
    }
}