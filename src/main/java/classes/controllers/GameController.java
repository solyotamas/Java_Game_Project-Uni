package classes.controllers;

import classes.game.GameBoard;
import classes.terrains.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class GameController {
    private Stage stage;
    private Scene scene;

    @FXML
    private Pane gamePane;

    @FXML
    private ListView<String> marketListView;

    public void addItemsToList() {
        ObservableList<String> items = FXCollections.observableArrayList("Animal", "Tree", "Bush",
                "SGrass", "Tree2");
        marketListView.setItems(items);
    }

    @FXML
    public void initialize() {
        //preloading images for faster start
        Ground.preloadGroundImages();
        Hill.preloadHillImage();
        River.preloadRiverImage();

        GameBoard gameBoard = new GameBoard(gamePane);
        gameBoard.setupBoard();
    }

    public void switchToSave(ActionEvent event) throws IOException {
        switchScene(event, "/fxmls/save_screen.fxml");
    }

    public void switchToMain(ActionEvent event) throws IOException {
        switchScene(event, "/fxmls/main_screen.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

}