package classes;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class SceneController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ListView<String> saveListView;

    public void switchToDifficulty(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxmls/difficulty_screen.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setMaximized(true);
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToMain(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxmls/main_screen.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setMaximized(true);
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToLoad(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxmls/load_screen.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setMaximized(true);
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void exit(ActionEvent event) throws IOException {
        Platform.exit();
        System.exit(0);
    }
}
