package classes.controllers;

import classes.Difficulty;
import classes.entities.additions.MusicPlayer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class ScreenController {
    private Stage stage;
    private Scene scene;

    @FXML
    private ListView<String> saveListView;

    @FXML
    private ToggleButton easyToggle;
    @FXML
    private ToggleButton mediumToggle;
    @FXML
    private ToggleButton hardToggle;

    @FXML
    public void initialize() {
        if (easyToggle != null && mediumToggle != null && hardToggle != null) {
            ToggleGroup difficultyGroup = new ToggleGroup();
            easyToggle.setToggleGroup(difficultyGroup);
            mediumToggle.setToggleGroup(difficultyGroup);
            hardToggle.setToggleGroup(difficultyGroup);
        }
        volumeIcon.setImage(new Image(getClass().getResource(MusicPlayer.getCurrentIconPath()).toExternalForm()));
    }

    // ==== Music
    @FXML
    private ImageView volumeIcon;

    @FXML
    private void toggleVolume() {
        MusicPlayer.toggleVolume();
        volumeIcon.setImage(new Image(getClass().getResource(MusicPlayer.getCurrentIconPath()).toExternalForm()));
    }
    // =====

    public void addItemsToList() {
        ObservableList<String> items = FXCollections.observableArrayList("Save Slot 1", "Save Slot 2", "Save Slot 3", "Save Slot 4", "Save Slot 5");
        saveListView.setItems(items);
    }

    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
        stage.show();
    }

    public void switchToDifficulty(ActionEvent event) throws IOException {
        switchScene(event, "/fxmls/difficulty_screen.fxml");
    }

    public void switchToLoad(ActionEvent event) throws IOException {
        switchScene(event, "/fxmls/load_screen.fxml");
    }

    public void switchToMain(ActionEvent event) throws IOException {
        switchScene(event, "/fxmls/main_screen.fxml");
    }

    public void switchToGame(ActionEvent event) throws IOException {
        Difficulty selectedDifficulty = Difficulty.EASY;

        if (easyToggle.isSelected()) {
            selectedDifficulty = Difficulty.EASY;
        } else if (mediumToggle.isSelected()) {
            selectedDifficulty = Difficulty.MEDIUM;
        } else if (hardToggle.isSelected()) {
            selectedDifficulty = Difficulty.HARD;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/game_screen.fxml"));
        Parent root = loader.load();

        GameController controller = loader.getController();
        controller.setDifficulty(selectedDifficulty);

        controller.startGame();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void exit() {
        Platform.exit();
        System.exit(0);
    }
}