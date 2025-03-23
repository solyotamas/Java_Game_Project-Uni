package classes.controllers;

import classes.entities.animals.Animal;
import classes.entities.animals.herbivores.Elephant;
import classes.game.GameBoard;
import classes.game.GameEngine;

import classes.landforms.Lake;
import classes.landforms.Landform;
import classes.landforms.plants.Bush;
import classes.landforms.plants.Grass;
import classes.landforms.plants.Tree;
import classes.terrains.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
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

import static classes.Difficulty.EASY;


public class GameController {
    private GameBoard gameBoard;
    private GameEngine gameEngine;
    private static final int TILE_SIZE = 30;

    //For Gameboard
    @FXML
    private Pane terrainLayer;
    @FXML
    private Pane environmentLayer;
    @FXML
    private Pane entityLayer;
    @FXML
    private Pane uiLayer;
    @FXML
    private Pane shopPane;
    @FXML
    private Button marketButton;

    //top and bottom bar UI
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





    private void buyLandform(Landform landform, String imagePath) {
        shopPane.setVisible(false);

        Image plantImage = new Image(getClass().getResource(imagePath).toExternalForm());
        ImageView ghostImage = new ImageView(plantImage);
        ghostImage.setOpacity(0.5);

        //ghostImage.setMouseTransparent(true);
        ghostImage.setFitWidth(TILE_SIZE * landform.getWidthInTiles());
        ghostImage.setFitHeight(TILE_SIZE * landform.getHeightInTiles());

        gameBoard.getEnvironmentLayer().getChildren().add(ghostImage);

        uiLayer.setMouseTransparent(true);
        entityLayer.setMouseTransparent(true);
        gameBoard.getEnvironmentLayer().setOnMouseMoved(e -> {
            Point2D localPoint = gameBoard.getEnvironmentLayer().sceneToLocal(e.getSceneX(), e.getSceneY());
            double snappedX = Math.floor(localPoint.getX() / TILE_SIZE) * TILE_SIZE;
            double snappedY = Math.floor(localPoint.getY() / TILE_SIZE) * TILE_SIZE;

            int maxX = (gameBoard.getColumns() - landform.getWidthInTiles()) * TILE_SIZE;
            int maxY = (gameBoard.getRows() - landform.getHeightInTiles()) * TILE_SIZE;
            snappedX = Math.max(0, Math.min(snappedX, maxX));
            snappedY = Math.max(0, Math.min(snappedY, maxY));

            ghostImage.setLayoutX(snappedX);
            ghostImage.setLayoutY(snappedY);
        });

        gameBoard.getEnvironmentLayer().setOnMouseClicked(e -> {
            int tileX = ((int) e.getX()) / TILE_SIZE;
            int tileY = ((int) e.getY()) / TILE_SIZE;

            boolean canPlace = gameBoard.canPlaceItem(landform, tileX, tileY);

            if (canPlace) {
                gameBoard.placeLandform(landform, tileX, tileY);
            } else {
                System.out.println("Cannot place here.");
            }

            gameBoard.getEnvironmentLayer().getChildren().remove(ghostImage);
            gameBoard.getEnvironmentLayer().setOnMouseMoved(null);
            gameBoard.getEnvironmentLayer().setOnMouseClicked(null);

            uiLayer.setMouseTransparent(false);
            entityLayer.setMouseTransparent(false);
        });

    }
    @FXML
    public void buyBush() {
        buyLandform(new Bush(0, 0), "/images/bush1.png");
    }
    @FXML
    public void buyTree() {
        buyLandform(new Tree(0, 0), "/images/tree2.png");
    }
    @FXML
    public void buyLake() {
        buyLandform(new Lake(0, 0), "/images/lake.png");
    }
    @FXML
    public void buyGrass() {
        buyLandform(new Grass(0, 0), "/images/grass.png");
    }


    //Class<? extends Animal> animalClass: nem akarom elore letrehozni az
    // instancet mert akkor nem tudom a egerre spawnolni
    //kinda ugly idk if it can be improved, ghostimage still whole sprite need to fix
    //needs work
    private void buyAnimal(Class<? extends Animal> animalClass, String imagePath) {
        shopPane.setVisible(false);

        Image animalImage = new Image(getClass().getResource(imagePath).toExternalForm());
        ImageView ghostImage = new ImageView(animalImage);
        ghostImage.setOpacity(0.5);
        ghostImage.setMouseTransparent(true);
        ghostImage.setFitWidth(50);
        ghostImage.setFitHeight(50);


        gameBoard.getEntityLayer().getChildren().add(ghostImage);

        uiLayer.setMouseTransparent(true);
        gameBoard.getEntityLayer().setOnMouseMoved(e -> {
            ghostImage.setLayoutX(e.getX() - (ghostImage.getFitWidth() / 2));
            ghostImage.setLayoutY(e.getY() - (ghostImage.getFitHeight() / 2));
        });

        gameBoard.getEntityLayer().setOnMouseClicked(e -> {
            double placeX = e.getX() - (ghostImage.getFitWidth() / 2);
            double placeY = e.getY() - (ghostImage.getFitHeight() / 2);

            try {
                Animal animalInstance = animalClass
                        .getDeclaredConstructor(double.class, double.class)
                        .newInstance(placeX, placeY);

                // Set actual position here:
                animalInstance.setLayoutX(placeX);
                animalInstance.setLayoutY(placeY);

                gameBoard.getEntityLayer().getChildren().add(animalInstance);
                gameEngine.buyAnimal(animalInstance);

                System.out.println("Added animal at " + placeX + ", " + placeY);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            gameBoard.getEntityLayer().getChildren().remove(ghostImage);
            gameBoard.getEntityLayer().setOnMouseMoved(null);
            gameBoard.getEntityLayer().setOnMouseClicked(null);

            uiLayer.setMouseTransparent(false);
        });

    }
    @FXML
    public void buyElephant(){

        buyAnimal(Elephant.class, "/images/elephant.png");


    }
    @FXML
    public void buyRhino(){

    }
    @FXML
    public void buyHippo(){

    }
    @FXML
    public void buyBuffalo(){

    }
    @FXML
    public void buyZebra(){

    }
    @FXML
    public void buyKangaroo(){

    }
    @FXML
    public void buyTurtle(){

    }
    @FXML
    public void buyLion(){

    }
    @FXML
    public void buyTiger(){

    }
    @FXML
    public void buyPanther(){

    }
    @FXML
    public void buyVulture(){

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

        this.gameBoard = new GameBoard(terrainLayer, environmentLayer, entityLayer, uiLayer, shopPane, marketButton);
        gameBoard.setupGroundBoard();

        //IDE MAJD KELL RENDESEN A PARAMÉTEREK
        this.gameEngine = new GameEngine(this, EASY);
        gameEngine.gameLoop();





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
    public void preloadImages(){
        Ground.preloadGroundImages();
        Hill.preloadHillImages();
        Floor.preloadFloorImages();
        Fence.preloadFenceImages();
        River.preloadRiverImage();
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