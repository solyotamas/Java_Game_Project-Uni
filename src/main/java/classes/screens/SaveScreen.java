package classes.screens;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SaveScreen extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoadScreen.class.getResource("/fxmls/save_screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Szafari");
        stage.setScene(scene);
        stage.setMaximized(true);

        //stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
