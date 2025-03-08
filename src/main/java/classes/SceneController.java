package classes;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    @FXML
    private ListView<String> saveListView;

    public void addItemsToList() {
        ObservableList<String> items = FXCollections.observableArrayList("Save Slot 1", "Save Slot 2", "Save Slot 3",
                "Save Slot 4", "Save Slot 5", "Save Slot 1", "Save Slot 2", "Save Slot 3", "Save Slot 4", "Save Slot 5",
                "Save Slot 1", "Save Slot 2", "Save Slot 3", "Save Slot 4", "Save Slot 5"
        );
        saveListView.setItems(items);
    }

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

    public void switchToGame(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/game_screen.fxml"));
        Parent root = loader.load();

        GameScreenController gameScreenController = loader.getController();
        gameScreenController.loadLevel();

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setMaximized(true);
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void exit() {
        Platform.exit();
        System.exit(0);
    }
}
