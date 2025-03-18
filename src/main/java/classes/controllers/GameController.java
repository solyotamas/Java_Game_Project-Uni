package classes.controllers;

import classes.game.GameBoard;
import classes.placeables.Bush;
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

    @FXML
    private Button buyBushButton;
    private ImageView ghostImage;

    @FXML
    public void happens() {
        shopPane.setVisible(true);
        //shopPane.setManaged(false);
        shopPane.toFront();
    }

    @FXML
    public void buyBush() {
        Image plantImage = new Image(getClass().getResource("/images/bush1.png").toExternalForm());
        ImageView ghostImage = new ImageView(plantImage);
        ghostImage.setOpacity(0.3);
        ghostImage.setMouseTransparent(true); // Doesn’t block mouse input
        ghostImage.setFitWidth(30);
        ghostImage.setFitHeight(30);

        gamePane.getChildren().add(ghostImage);

        gamePane.setOnMouseMoved(e -> {
            int gridSize = 30;
            double snappedX = Math.floor(e.getX() / gridSize) * gridSize;
            double snappedY = Math.floor(e.getY() / gridSize) * gridSize;
            ghostImage.setLayoutX(snappedX);
            ghostImage.setLayoutY(snappedY);
        });

        gamePane.setOnMouseClicked(e -> {
            int gridSize = 30;
            int snappedX = (int)Math.floor(e.getX() / gridSize) * gridSize;
            int snappedY = (int)Math.floor(e.getY() / gridSize) * gridSize;




            int tileX = snappedX / 30;
            int tileY = snappedY / 30;
            Terrain terrain = gameBoard.getTerrainAt(tileX, tileY);
            if (terrain == null) {
                System.out.println("No terrain found at these coordinates!");
            } else if (terrain.hasPlant()) {
                System.out.println("Terrain already has a plant placed.");
            } else {
                Bush bush = new Bush(tileX, tileY, 30);
                terrain.placePlant(bush);
            }
            System.out.println("Bush placed");
            //bush.toFront();
            //terrain.setOpacity(0.4);


            // Clean up ghost image and listeners
            gamePane.getChildren().remove(ghostImage);
            gamePane.setOnMouseMoved(null);
            gamePane.setOnMouseClicked(null);
        });
    }

    @FXML
    public void closeShopPane(){
        shopPane.setVisible(false);
        //shopPane.toBack();
    }

    public void preloadImages(){
        Ground.preloadGroundImages();
        Hill.preloadHillImage();
        Floor.preloadFloorImages();
        Fence.preloadFenceImages();
    }

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