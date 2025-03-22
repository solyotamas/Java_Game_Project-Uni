package classes.controllers;

import classes.game.GameBoard;
import classes.placeables.*;
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
    private void buyItem(String imagePath, int tileWidth, int tileHeight, String type) {
        shopPane.setVisible(false);

        int gridSize = 30;
        Image plantImage = new Image(getClass().getResource(imagePath).toExternalForm());
        ImageView ghostImage = new ImageView(plantImage);
        ghostImage.setOpacity(0.5);
        ghostImage.setMouseTransparent(true);
        ghostImage.setFitWidth(gridSize * tileWidth);
        ghostImage.setFitHeight(gridSize * tileHeight);

        gamePane.getChildren().add(ghostImage);

        gamePane.setOnMouseMoved(e -> {
            double snappedX = Math.floor(e.getX() / gridSize) * gridSize;
            double snappedY = Math.floor(e.getY() / gridSize) * gridSize;

            int maxX = (gameBoard.getColumns() - tileWidth) * gridSize;
            int maxY = (gameBoard.getRows() - tileHeight) * gridSize;
            snappedX = Math.max(0, Math.min(snappedX, maxX));
            snappedY = Math.max(0, Math.min(snappedY, maxY));

            ghostImage.setLayoutX(snappedX);
            ghostImage.setLayoutY(snappedY);
        });

        gamePane.setOnMouseClicked(e -> {
            int tileX = ((int) e.getX()) / gridSize;
            int tileY = ((int) e.getY()) / gridSize;

            boolean canPlace = switch (type) {
                case "Bush", "Grass" -> gameBoard.canPlaceBushGrass(tileX, tileY);
                case "Tree" -> gameBoard.canPlaceTree(tileX, tileY);
                case "Lake" -> gameBoard.canPlaceLake(tileX, tileY);
                default -> false;
            };

            if (canPlace) {
                switch (type) {
                    case "Grass" -> gameBoard.placeSingleTilePlant(new Grass(tileX, tileY), tileX, tileY);
                    case "Bush" -> gameBoard.placeSingleTilePlant(new Bush(tileX, tileY), tileX, tileY);
                    case "Tree" -> gameBoard.placeMultiTilePlant(new Tree(tileX, tileY), tileX, tileY, 2, 2);
                    case "Lake" -> gameBoard.placeMultiTilePlant(new Lake(tileX, tileY), tileX, tileY, 4, 2);
                }
            } else {
                System.out.println("Cannot place here.");
            }

            gamePane.getChildren().remove(ghostImage);
            gamePane.setOnMouseMoved(null);
            gamePane.setOnMouseClicked(null);
        });
    }

    @FXML
    public void buyBush() {
        buyItem("/images/bush1.png", 1, 1, "Bush");
    }

    @FXML
    public void buyTree() {
        buyItem("/images/tree2.png", 2, 2, "Tree");
    }

    @FXML
    public void buyLake() {
        buyItem("/images/lake.png", 4, 2, "Lake");
    }

    public void buyGrass() {
        buyItem("/images/grass.png", 1, 1, "Grass");
    }

    public void preloadImages(){
        Ground.preloadGroundImages();
        Hill.preloadHillImages();
        Floor.preloadFloorImages();
        Fence.preloadFenceImages();
        River.preloadRiverImage();
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