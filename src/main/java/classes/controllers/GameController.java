package classes.controllers;

import classes.entities.Direction;
import classes.entities.animals.Elephant;
import classes.entities.animals.Zebra;
import classes.game.GameBoard;
import classes.game.GameEngine;

import classes.landforms.Lake;
import classes.landforms.Landform;
import classes.landforms.plants.Bush;
import classes.landforms.plants.Grass;
import classes.landforms.plants.Tree;
import classes.terrains.*;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.scene.image.ImageView;
import javafx.util.Duration;

import static classes.Difficulty.EASY;


public class GameController {
    private GameBoard gameBoard;
    private GameEngine gameEngine;
    private static final int TILE_SIZE = 30;


    @FXML
    private Pane gamePane;
    @FXML
    private Button marketButton;
    @FXML
    private Pane shopPane;
    @FXML
    private Label gameTimeLabel;
    @FXML
    private Label herbivoreCountLabel;
    @FXML
    private Label carnivoreCountLabel;
    @FXML
    private Label jeepCountLabel;
    @FXML
    private Label touristCountLabel;
    @FXML
    private Button gameSpeedHourButton;
    @FXML
    private Button gameSpeedDayButton;


    @FXML
    public void speedGameToDay(){

    }

    @FXML
    public void speedGameToHour(){

    }

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
    private void buyItem(Landform landform, String imagePath) {
        shopPane.setVisible(false);

        Image plantImage = new Image(getClass().getResource(imagePath).toExternalForm());
        ImageView ghostImage = new ImageView(plantImage);
        ghostImage.setOpacity(0.5);
        ghostImage.setMouseTransparent(true);
        ghostImage.setFitWidth(TILE_SIZE * landform.getWidthInTiles());
        ghostImage.setFitHeight(TILE_SIZE * landform.getHeightInTiles());

        gamePane.getChildren().add(ghostImage);

        gamePane.setOnMouseMoved(e -> {
            double snappedX = Math.floor(e.getX() / TILE_SIZE) * TILE_SIZE;
            double snappedY = Math.floor(e.getY() / TILE_SIZE) * TILE_SIZE;

            int maxX = (gameBoard.getColumns() - landform.getWidthInTiles()) * TILE_SIZE;
            int maxY = (gameBoard.getRows() - landform.getHeightInTiles()) * TILE_SIZE;
            snappedX = Math.max(0, Math.min(snappedX, maxX));
            snappedY = Math.max(0, Math.min(snappedY, maxY));

            ghostImage.setLayoutX(snappedX);
            ghostImage.setLayoutY(snappedY);
        });

        gamePane.setOnMouseClicked(e -> {
            int tileX = ((int) e.getX()) / TILE_SIZE;
            int tileY = ((int) e.getY()) / TILE_SIZE;

            boolean canPlace = gameBoard.canPlaceItem(landform, tileX, tileY);

            if (canPlace) {
                gameBoard.placeItem(landform, tileX, tileY);
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
        buyItem(new Bush(0, 0), "/images/bush1.png");
    }
    @FXML
    public void buyTree() {
        buyItem(new Tree(0, 0), "/images/tree2.png");
    }
    @FXML
    public void buyLake() {
        buyItem(new Lake(0, 0), "/images/lake.png");
    }
    public void buyGrass() {
        buyItem(new Grass(0, 0), "/images/grass.png");
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

        //IDE MAJD KELL RENDESEN A PARAMÉTEREK
        this.gameEngine = new GameEngine(this, EASY);
        gameEngine.gameLoop();



        Zebra zebra = new Zebra(400, 400);
        gamePane.getChildren().add(zebra);
        zebra.toFront();
        gameEngine.buyHerbivore(zebra);
        Elephant elephant = new Elephant(800,800);
        gamePane.getChildren().add(elephant);
        gameEngine.buyHerbivore(elephant);




        // Random direction change timeline every 2-4 seconds
        Timeline randomDirectionChange = new Timeline(new javafx.animation.KeyFrame(Duration.seconds(5), e -> {
            Direction[] directions = Direction.values();
            int randomIndex = new java.util.Random().nextInt(directions.length);
            zebra.setCurrentDirection(directions[randomIndex]);
            elephant.setCurrentDirection(directions[randomIndex]);
        }));
        randomDirectionChange.setCycleCount(Timeline.INDEFINITE);
        randomDirectionChange.play();

    }

    public void updateDisplay(double time, int carnivores, int herbivores, int jeeps, int tourists){
        //STATS
        int days = (int) time / 24;
        int hours = (int) time % 24;

        gameTimeLabel.setText("Day: " + days + " Hour: " + hours);
        carnivoreCountLabel.setText(carnivores + "");
        herbivoreCountLabel.setText(herbivores + "");
        jeepCountLabel.setText(jeeps + "");
        touristCountLabel.setText(tourists + "");


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