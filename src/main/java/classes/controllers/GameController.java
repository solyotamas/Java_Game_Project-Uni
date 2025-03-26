package classes.controllers;

import classes.entities.animals.Animal;
import classes.entities.animals.carnivores.Lion;
import classes.entities.animals.carnivores.Panther;
import classes.entities.animals.carnivores.Tiger;
import classes.entities.animals.carnivores.Vulture;
import classes.entities.animals.herbivores.*;
import classes.entities.human.Human;
import classes.entities.human.Ranger;
import classes.game.GameBoard;
import classes.game.GameEngine;

import classes.landforms.Lake;
import classes.landforms.Landform;
import classes.landforms.Road;
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
    private Pane dynamicLayer;
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
    private Label ticketPriceLabel;


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





    private void buyLandform(Class<? extends Landform> landformClass, Image chosen) {
        //just because of a bug sometimes
        gameBoard.getDynamicLayer().getChildren().removeIf(node -> node.getOpacity() == 0.5);

        //shop disappear
        shopPane.setVisible(false);


        //For ghostImage pic to be equivalent of the image of the instance that will be placed
        Landform tempInstance = null;
        try {
            tempInstance = landformClass
                    .getDeclaredConstructor(double.class, double.class, double.class, Image.class)
                    .newInstance(0.0, 0.0, 0.0, chosen);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ImageView ghostImage = new ImageView(chosen);
        ghostImage.setOpacity(0.5);
        ghostImage.setFitWidth(TILE_SIZE * tempInstance.getWidthInTiles());
        ghostImage.setFitHeight(TILE_SIZE * tempInstance.getHeightInTiles());
        gameBoard.getDynamicLayer().getChildren().add(ghostImage);


        //snapping ghost image to terrains
        uiLayer.setMouseTransparent(true);
        gameBoard.getDynamicLayer().setOnMouseMoved(e -> {
            double snapX = Math.floor(e.getX() / TILE_SIZE) * TILE_SIZE;
            double snapY = Math.floor(e.getY() / TILE_SIZE) * TILE_SIZE;
            ghostImage.setLayoutX(snapX);
            ghostImage.setLayoutY(snapY);
        });


        final Landform finalTempInstance = tempInstance;
        gameBoard.getDynamicLayer().setOnMouseClicked(e -> {
            int tileX = (int) e.getX() / TILE_SIZE;
            int tileY = (int) e.getY() / TILE_SIZE;

            double depth = tileY * TILE_SIZE + finalTempInstance.getHeightInTiles() * TILE_SIZE;

            // Special depth for lakes and roads
            if (Lake.class.isAssignableFrom(landformClass)) {
                depth = Double.MIN_VALUE;
            } else if (Road.class.isAssignableFrom(landformClass)) {
                depth = Double.MIN_VALUE + 1;
            }

            try {
                Landform placedLandform = landformClass
                        .getDeclaredConstructor(double.class, double.class, double.class, Image.class)
                        .newInstance(tileX * TILE_SIZE, tileY * TILE_SIZE, depth, chosen);

                if (gameBoard.canPlaceItem(placedLandform, tileX, tileY)) {

                    gameBoard.placeLandform(placedLandform, tileX, tileY);
                    gameBoard.getDynamicLayer().getChildren().add(placedLandform);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            gameBoard.getDynamicLayer().getChildren().remove(ghostImage);
            gameBoard.getDynamicLayer().setOnMouseMoved(null);
            gameBoard.getDynamicLayer().setOnMouseClicked(null);

            uiLayer.setMouseTransparent(false);
        });

    }
    @FXML
    public void buyBush() {
        buyLandform(Bush.class, Bush.getRandomBushImage());
    }
    @FXML
    public void buyTree() {
        buyLandform(Tree.class, Tree.getRandomTreeImage());
    }
    @FXML
    public void buyLake() {
        buyLandform(Lake.class, Lake.lakePicture);
    }
    @FXML
    public void buyGrass() {
        buyLandform(Grass.class, Grass.grassPicture);
    }
    @FXML
    public void buyRoad() {
        buyLandform(Road.class, Road.roadImages[0]);
    }


    //Class<? extends Animal> animalClass mert ugy lehet atadni jol az x y -t
    private void buyAnimal(Class<? extends Animal> animalClass, String imagePath) {
        shopPane.setVisible(false);

        Image animalImage = new Image(getClass().getResource(imagePath).toExternalForm());
        ImageView ghostImage = new ImageView(animalImage);
        ghostImage.setOpacity(0.5);
        ghostImage.setMouseTransparent(true);
        ghostImage.setFitWidth(50);
        ghostImage.setFitHeight(50);


        gameBoard.getDynamicLayer().getChildren().add(ghostImage);

        //disable clicks on top layer
        uiLayer.setMouseTransparent(true);

        gameBoard.getDynamicLayer().setOnMouseMoved(e -> {
            ghostImage.setLayoutX(e.getX() - (ghostImage.getFitWidth() / 2));
            ghostImage.setLayoutY(e.getY() - (ghostImage.getFitHeight() / 2));
        });

        gameBoard.getDynamicLayer().setOnMouseClicked(e -> {


            try {

                double placeX = e.getX();
                double placeY = e.getY();
                Animal animalInstance = animalClass
                        .getDeclaredConstructor(double.class, double.class)
                        .newInstance(placeX, placeY);


                gameBoard.getDynamicLayer().getChildren().add(animalInstance);
                gameEngine.buyAnimal(animalInstance);

                System.out.println("Added animal at " + placeX + ", " + placeY);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            gameBoard.getDynamicLayer().getChildren().remove(ghostImage);
            gameBoard.getDynamicLayer().setOnMouseMoved(null);
            gameBoard.getDynamicLayer().setOnMouseClicked(null);

            //enable clicks on top layer
            uiLayer.setMouseTransparent(false);
        });

    }
    @FXML
    public void buyElephant(){
        buyAnimal(Elephant.class, "/images/elephant.png");
    }
    @FXML
    public void buyRhino(){
        buyAnimal(Rhino.class, "/images/rhino.png");
    }
    @FXML
    public void buyHippo(){
        buyAnimal(Hippo.class, "/images/hippo.png");
    }
    @FXML
    public void buyBuffalo(){
        buyAnimal(Buffalo.class, "/images/buffalo.png");
    }
    @FXML
    public void buyZebra(){
        buyAnimal(Zebra.class, "/images/zebra.png");
    }
    @FXML
    public void buyKangaroo(){
        buyAnimal(Kangaroo.class, "/images/kangaroo.png");
    }
    @FXML
    public void buyTurtle(){
        buyAnimal(Turtle.class, "/images/turtle.png");
    }
    @FXML
    public void buyLion(){
        buyAnimal(Lion.class, "/images/lion.png");
    }
    @FXML
    public void buyTiger(){
        buyAnimal(Tiger.class, "/images/tiger.png");
    }
    @FXML
    public void buyPanther(){
        buyAnimal(Panther.class, "/images/panther.png");
    }
    @FXML
    public void buyVulture(){
        buyAnimal(Vulture.class, "/images/vulture.png");
    }

    @FXML
    private void buyRanger() {
        shopPane.setVisible(false);

        Image rangerImage = new Image(getClass().getResource("/images/ranger.png").toExternalForm());
        ImageView ghostImage = new ImageView(rangerImage);
        ghostImage.setOpacity(0.5);
        ghostImage.setMouseTransparent(true);
        ghostImage.setFitWidth(50);
        ghostImage.setFitHeight(50);


        dynamicLayer.getChildren().add(ghostImage);

        //disable clicks on top layer
        uiLayer.setMouseTransparent(true);

        dynamicLayer.setOnMouseMoved(e -> {
            ghostImage.setLayoutX(e.getX() - (ghostImage.getFitWidth() / 2));
            ghostImage.setLayoutY(e.getY() - (ghostImage.getFitHeight() / 2));
        });

        dynamicLayer.setOnMouseClicked(e -> {
            try {
                double placeX = e.getX();
                double placeY = e.getY();

                Ranger rangerInstance = new Ranger(placeX, placeY);

                uiLayer.getChildren().add(rangerInstance);
                gameEngine.buyRanger(rangerInstance);

                System.out.println("Added ranger at " + placeX + ", " + placeY);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            dynamicLayer.getChildren().remove(ghostImage);
            dynamicLayer.setOnMouseMoved(null);
            dynamicLayer.setOnMouseClicked(null);

            //enable clicks on top layer
            uiLayer.setMouseTransparent(false);
        });

    }

    //starting game
    //TODO
    // - make a game loop, maybe game factory to separate placing the plants, track placed plants somewhere
    // and just manage the UI here
    // -
    @FXML
    public void initialize() {
        //preloading images for faster start
        // swapped for cleaner code using statics methods inside classes
        //-------------------------------

        this.gameBoard = new GameBoard(terrainLayer, dynamicLayer, uiLayer, shopPane, marketButton);
        gameBoard.setupGroundBoard();

        //IDE MAJD KELL RENDESEN A PARAMÉTEREK
        this.gameEngine = new GameEngine(this, EASY, gameBoard);
        gameEngine.gameLoop();





    }


    public void updateDisplay(double time, int carnivores, int herbivores, int jeeps, int tourists, int ticketPrice){
        //STATS
        int days = (int) time / 24;
        int hours = (int) time % 24;

        gameTimeLabel.setText("Day: " + days + " Hour: " + hours);
        carnivoreCountLabel.setText(carnivores + "");
        herbivoreCountLabel.setText(herbivores + "");
        jeepCountLabel.setText(jeeps + "");
        touristCountLabel.setText(tourists + "");
        ticketPriceLabel.setText(ticketPrice + "");



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