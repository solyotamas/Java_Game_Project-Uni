package classes.entities.additions;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class MusicPlayer {
    private static MediaPlayer mediaPlayer;
    private static VolumeLevel currentLevel = VolumeLevel.MEDIUM;

    public static void init(String resourcePath) {
        if (mediaPlayer == null) {
            URL url = MusicPlayer.class.getResource(resourcePath);
            if (url == null) throw new IllegalArgumentException("Nem található: " + resourcePath);
            mediaPlayer = new MediaPlayer(new Media(url.toExternalForm()));
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            setVolume(currentLevel.getVolume());
        }
    }

    public static void play() {
        if (mediaPlayer != null) mediaPlayer.play();
    }

    public static void stop() {
        if (mediaPlayer != null) mediaPlayer.stop();
    }

    public static void toggleVolume() {
        currentLevel = currentLevel.next();
        setVolume(currentLevel.getVolume());
    }

    public static void setVolume(double v) {
        if (mediaPlayer != null) mediaPlayer.setVolume(v);
        currentLevel = VolumeLevel.fromVolume(v);
    }

    public static double getVolume() {
        return mediaPlayer != null ? mediaPlayer.getVolume() : currentLevel.getVolume();
    }

    public static VolumeLevel getVolumeLevel() {
        return currentLevel;
    }

    public static String getCurrentIconPath() {
        return currentLevel.getIconPath();
    }
}