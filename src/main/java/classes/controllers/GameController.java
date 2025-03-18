package classes.controllers;

import classes.game.GameBoard;
import classes.terrains.Fence;
import classes.terrains.Floor;
import classes.terrains.Ground;
import classes.terrains.Hill;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class GameController {
    private Stage stage;
    private Scene scene;

    @FXML
    private Pane gamePane;

    @FXML
    private Button valami;

    @FXML
    public void happens() {
        System.out.println("Market button clicked!");
    }

    @FXML
    public void initialize() {
        //preloading images for faster start
        Ground.preloadGroundImages();
        Hill.preloadHillImage();
        Floor.preloadFloorImages();
        Fence.preloadFenceImages();
        //-------------------------------

        GameBoard gameBoard = new GameBoard(gamePane, valami);
        gameBoard.setupBoard();
        valami.toFront();
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