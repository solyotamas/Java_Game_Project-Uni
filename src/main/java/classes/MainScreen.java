package classes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainScreen extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/main_screen.fxml"));
            Parent root = loader.load();
            SceneController controller = loader.getController();
            Scene scene = new Scene(root);
            stage.setTitle("Szafari");
            stage.setScene(scene);
            stage.setMaximized(true);

            //stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch();
    }
}
