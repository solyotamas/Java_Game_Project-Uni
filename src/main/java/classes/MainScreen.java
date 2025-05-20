package classes;

import classes.entities.additions.MusicPlayer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainScreen extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MusicPlayer.init("/music/main.mp3");
        MusicPlayer.play();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/main_screen.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            stage.setTitle("Szafari");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
