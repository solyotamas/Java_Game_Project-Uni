package classes.controllers;

import classes.game.GameBoard;
import classes.placeables.animals.*;
import classes.terrains.Ground;
import classes.terrains.Hill;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;

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

        //Moving animals beta
        Elephant eli1 = new Elephant(100, 100);
        eli1.draw(gamePane);  // Draw the elephant on the game pane

        Trestle tres1 = new Trestle(1300, 30);
        tres1.draw(gamePane);

        Gazelle gaz1 = new Gazelle(200, 180);
        gaz1.draw(gamePane);

        Lynx lynx1 = new Lynx(1700, 880);
        lynx1.draw(gamePane);

        Kecske kecs1 = new Kecske(1270, 880);
        kecs1.draw(gamePane);

        Lion lio1 = new Lion(1270, 880);
        lio1.draw(gamePane);

        Ostrich ost1 = new Ostrich(90, 880);
        ost1.draw(gamePane);

        Turtle tur1 = new Turtle(90, 80);
        tur1.draw(gamePane);

        Giraffe gir1 = new Giraffe(1270, 880);
        gir1.draw(gamePane);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e -> {
                    eli1.move(6, 0);
                    tres1.move(-6, 0);
                    gaz1.move(0, 6);
                    lynx1.move(0, -6);
                    kecs1.move(-6, 0);
                    lio1.move(-3, -6);
                    ost1.move(3, -6);
                    tur1.move(3, 6);
                    gir1.move(0, -6);
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);  // Repeat forever
        timeline.play();  // Start the animation
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