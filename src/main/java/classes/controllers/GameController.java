package classes.controllers;

import classes.game.GameBoard;
import classes.placeables.Bush;
import classes.placeables.Lake;
import classes.placeables.Tree;
import classes.terrains.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.image.ImageView;


public class GameController {
    private GameBoard gameBoard;


    @FXML
    private Pane gamePane;
    @FXML
    private Button marketButton;
    @FXML
    private Pane shopPane;


    //Market appear, disappear
    @FXML
    public void happens() {
        shopPane.setVisible(true);
        shopPane.toFront();
    }
    @FXML
    public void closeShopPane(){
        shopPane.setVisible(false);
    }



    //Market button actions
    //todo
    // need to simplify
    @FXML
    public void buyBush() {
        shopPane.setVisible(false);

        Image plantImage = new Image(getClass().getResource("/images/bush1.png").toExternalForm());
        ImageView ghostImage = new ImageView(plantImage);
        ghostImage.setOpacity(0.5);
        ghostImage.setMouseTransparent(true);
        ghostImage.setFitWidth(30);
        ghostImage.setFitHeight(30);

        gamePane.getChildren().add(ghostImage);

        gamePane.setOnMouseMoved(e -> {
            int gridSize = 30;
            double snappedX = Math.floor(e.getX() / gridSize) * gridSize;
            double snappedY = Math.floor(e.getY() / gridSize) * gridSize;

            int maxX = (gameBoard.getColumns() - 1) * gridSize;
            int maxY = (gameBoard.getRows() - 1) * gridSize;
            snappedX = Math.max(0, Math.min(snappedX, maxX));
            snappedY = Math.max(0, Math.min(snappedY, maxY));

            ghostImage.setLayoutX(snappedX);
            ghostImage.setLayoutY(snappedY);
        });

        gamePane.setOnMouseClicked(e -> {

            int tileX = ((int) e.getX()) / 30;
            int tileY = ((int) e.getY()) / 30;

            if (gameBoard.canPlaceBush(tileX, tileY)) {
                Bush bush = new Bush(tileX, tileY);
                gameBoard.placeSingleTilePlant(bush, tileX, tileY);
            } else {
                System.out.println("Cannot place tree here.");
            }


            // Clean up ghost image and listeners
            gamePane.getChildren().remove(ghostImage);
            gamePane.setOnMouseMoved(null);
            gamePane.setOnMouseClicked(null);
        });
    }
    @FXML
    public void buyTree() {
        shopPane.setVisible(false);

        Image treeImage = new Image(getClass().getResource("/images/tree2.png").toExternalForm());
        ImageView ghostImage = new ImageView(treeImage);
        ghostImage.setOpacity(0.5);
        ghostImage.setMouseTransparent(true);
        ghostImage.setFitWidth(30);
        ghostImage.setFitHeight(60);

        gamePane.getChildren().add(ghostImage);

        gamePane.setOnMouseMoved(e -> {
            int gridSize = 30;
            double snappedX = Math.floor(e.getX() / gridSize) * gridSize;
            double snappedY = Math.floor(e.getY() / gridSize) * gridSize;

            int maxX = (gameBoard.getColumns() - 1) * gridSize;
            int maxY = (gameBoard.getRows() - 2) * gridSize;
            snappedX = Math.max(0, Math.min(snappedX, maxX));
            snappedY = Math.max(0, Math.min(snappedY, maxY));

            ghostImage.setLayoutX(snappedX);
            ghostImage.setLayoutY(snappedY);
        });

        gamePane.setOnMouseClicked(e -> {
            int tileX = ((int) e.getX()) / 30;
            int tileY = ((int) e.getY()) / 30;

            if (gameBoard.canPlaceTree(tileX, tileY)) {
                Tree tree = new Tree(tileX, tileY);
                gameBoard.placeMultiTilePlant(tree, tileX, tileY, 1, 2);
            } else {
                System.out.println("Cannot place tree here.");
            }

            gamePane.getChildren().remove(ghostImage);
            gamePane.setOnMouseMoved(null);
            gamePane.setOnMouseClicked(null);
        });
    }
    @FXML
    public void buyLake() {
        shopPane.setVisible(false);

        Image lakeImage = new Image(getClass().getResource("/images/lake.png").toExternalForm());
        ImageView ghostImage = new ImageView(lakeImage);
        ghostImage.setOpacity(0.5);
        ghostImage.setMouseTransparent(true);
        ghostImage.setFitWidth(60);
        ghostImage.setFitHeight(30);

        gamePane.getChildren().add(ghostImage);

        gamePane.setOnMouseMoved(e -> {
            int gridSize = 30;
            double snappedX = Math.floor(e.getX() / gridSize) * gridSize;
            double snappedY = Math.floor(e.getY() / gridSize) * gridSize;

            int maxX = (gameBoard.getColumns() - 2) * gridSize;
            int maxY = (gameBoard.getRows() - 1) * gridSize;
            snappedX = Math.max(0, Math.min(snappedX, maxX));
            snappedY = Math.max(0, Math.min(snappedY, maxY));

            ghostImage.setLayoutX(snappedX);
            ghostImage.setLayoutY(snappedY);
        });

        gamePane.setOnMouseClicked(e -> {
            int tileX = ((int) e.getX()) / 30;
            int tileY = ((int) e.getY()) / 30;

            if (gameBoard.canPlaceLake(tileX, tileY)) {
                Lake lake = new Lake(tileX, tileY);
                gameBoard.placeMultiTilePlant(lake, tileX, tileY, 2, 1);
            } else {
                System.out.println("Cannot place lake here.");
            }

            gamePane.getChildren().remove(ghostImage);
            gamePane.setOnMouseMoved(null);
            gamePane.setOnMouseClicked(null);
        });
    }



    public void preloadImages(){
        Ground.preloadGroundImages();
        Hill.preloadHillImage();
        Floor.preloadFloorImages();
        Fence.preloadFenceImages();
    }


    //starting game
    //TODO
    // - make a game loop, maybe game factory to separate placing the plants, track placed plants somewhere
    // and just manage the UI here
    // -
    @FXML
    public void initialize() {
        //preloading images for faster start
            preloadImages();
        //-------------------------------

        this.gameBoard = new GameBoard(gamePane, shopPane, marketButton);
        gameBoard.setupBoard();

    }




    //SWITCHING SCENES
    public void switchToSave(ActionEvent event) throws IOException {
        switchScene(event, "/fxmls/save_screen.fxml");
    }
    public void switchToMain(ActionEvent event) throws IOException {
        switchScene(event, "/fxmls/main_screen.fxml");
    }
    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }


}