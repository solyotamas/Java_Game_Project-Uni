package classes.controllers;

import classes.entities.animals.Animal;
import classes.entities.animals.carnivores.Lion;
import classes.entities.animals.carnivores.Panther;
import classes.entities.animals.carnivores.Tiger;
import classes.entities.animals.carnivores.Vulture;
import classes.entities.animals.herbivores.*;
import classes.entities.human.Ranger;
import classes.game.GameBoard;
import classes.game.GameEngine;

import classes.landforms.Lake;
import classes.landforms.Landform;
import classes.landforms.Road;
import classes.landforms.plants.Bush;
import classes.landforms.plants.Grass;
import classes.landforms.plants.Plant;
import classes.landforms.plants.Tree;
import classes.terrains.Ground;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

import javafx.scene.image.ImageView;

import static classes.Difficulty.EASY;


public class GameController {
    private GameEngine gameEngine;

    //info panel
    private Pane infoWindow;
    private static Animal currentAnimalInfoPane = null;

    //stats
    private final int ROWS = 31;
    private final int COLUMNS = 64;
    private final int TILE_SIZE = 30;


    //For Gameboard
    @FXML
    private Pane terrainLayer;
    @FXML
    private Pane uiLayer;
    @FXML
    private Pane ghostLayer;
    @FXML
    private Pane shopPane;
    @FXML
    private Button marketButton;

    //Top and bottom bar UI
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
    private Label moneyLabel;

    @FXML
    public void initialize() {
        this.gameEngine = new GameEngine(this, EASY, terrainLayer, uiLayer, ghostLayer);
        //gameEngine.savePlants();
        gameEngine.gameLoop();
    }



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
    }
    @FXML
    public void closeShopPane(){
        shopPane.setVisible(false);
    }


    private void buyLandform(Class<? extends Landform> landformClass, Image chosen) {
        closeShopPane();
        ghostLayer.setVisible(true);

        boolean isRoad = Road.class.isAssignableFrom(landformClass);
        int[] remainingRoads = isRoad ? new int[]{10} : new int[]{1}; //Counter in array because you cant change primitive variables in lambda

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
        ghostLayer.getChildren().add(ghostImage);

        //Snapping ghost image to terrains
        ghostLayer.setMouseTransparent(false);
        ghostLayer.setOnMouseMoved(e -> {
            double snapX = Math.floor(e.getX() / TILE_SIZE) * TILE_SIZE;
            double snapY = Math.floor(e.getY() / TILE_SIZE) * TILE_SIZE;
            ghostImage.setLayoutX(snapX);
            ghostImage.setLayoutY(snapY);
        });

        final Landform finalTempInstance = tempInstance;
        ghostLayer.setOnMouseClicked(e -> {
            int tileX = (int) e.getX() / TILE_SIZE;
            int tileY = (int) e.getY() / TILE_SIZE;

            double depth = tileY * TILE_SIZE + finalTempInstance.getHeightInTiles() * TILE_SIZE;

            //Special depth for lakes and roads
            if (Lake.class.isAssignableFrom(landformClass)) {
                depth = Double.MIN_VALUE;
            } else if (isRoad) {
                depth = Double.MIN_VALUE + 1;
            }

            try {
                Landform placedLandform = landformClass
                        .getDeclaredConstructor(double.class, double.class, double.class, Image.class)
                        .newInstance(tileX * TILE_SIZE, tileY * TILE_SIZE, depth, chosen);

                if (gameEngine.getGameBoard().canPlaceLandform(placedLandform, tileX, tileY)) {
                    gameEngine.getGameBoard().placeLandform(placedLandform, tileX, tileY);
                    uiLayer.getChildren().add(placedLandform);
                    if (placedLandform instanceof Plant) {
                        gameEngine.buyPlant(placedLandform);
                        System.out.println("buyPlant called");
                    }
                    remainingRoads[0]--;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //If road, place 10
            if (!isRoad || remainingRoads[0] <= 0) {
                ghostLayer.getChildren().remove(ghostImage);
                ghostLayer.setOnMouseMoved(null);
                ghostLayer.setOnMouseClicked(null);
                uiLayer.setMouseTransparent(false);

                //ghostLayer.setVisible(false);
                ghostLayer.setMouseTransparent(true);
            }
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
    @FXML
    public void buyJeep() {{
        gameEngine.addJeep();
        closeShopPane();
    }}


    //Class<? extends Animal> animalClass mert ugy lehet atadni jol az x y -t
    private void buyAnimal(Class<? extends Animal> animalClass, String imagePath) {
        closeShopPane();
        ghostLayer.setVisible(true);
        ghostLayer.setMouseTransparent(false);

        Image animalImage = new Image(getClass().getResource(imagePath).toExternalForm());
        ImageView ghostImage = new ImageView(animalImage);
        ghostImage.setOpacity(0.5);
        ghostImage.setMouseTransparent(true);

        ghostImage.setFitWidth(40);
        ghostImage.setPreserveRatio(true);


        ghostLayer.getChildren().add(ghostImage);

        //Disable clicks on top layer
        uiLayer.setMouseTransparent(true);

        ghostLayer.setOnMouseMoved(e -> {
            ghostImage.setLayoutX(e.getX() - (ghostImage.getFitWidth() / 2));
            ghostImage.setLayoutY(e.getY() - (ghostImage.getFitHeight() / 2));
        });

        ghostLayer.setOnMouseClicked(e -> {
            try {
                double placeX = e.getX();
                double placeY = e.getY();
                Animal animalInstance = animalClass
                        .getDeclaredConstructor(double.class, double.class)
                        .newInstance(placeX, placeY);


                animalInstance.setOnMouseClicked(event -> showInfoWindow(animalInstance, event.getSceneX(), event.getSceneY()));
                uiLayer.getChildren().add(animalInstance);
                gameEngine.buyAnimal(animalInstance);

                //System.out.println("Added animal at " + placeX + ", " + placeY);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            ghostLayer.getChildren().remove(ghostImage);
            ghostLayer.setOnMouseMoved(null);
            ghostLayer.setOnMouseClicked(null);

            //ghostLayer.setVisible(false);
            ghostLayer.setMouseTransparent(true);

            //Enable clicks on top layer
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
        closeShopPane();

        ghostLayer.setVisible(true);
        ghostLayer.setMouseTransparent(false);

        Image rangerImage = new Image(getClass().getResource("/images/ranger.png").toExternalForm());
        ImageView ghostImage = new ImageView(rangerImage);
        ghostImage.setOpacity(0.5);
        ghostImage.setMouseTransparent(true);
        ghostImage.setFitWidth(50);
        ghostImage.setFitHeight(50);


        ghostLayer.getChildren().add(ghostImage);

        //Disable clicks on top layer
        uiLayer.setMouseTransparent(true);

        ghostLayer.setOnMouseMoved(e -> {
            ghostImage.setLayoutX(e.getX() - (ghostImage.getFitWidth() / 2));
            ghostImage.setLayoutY(e.getY() - (ghostImage.getFitHeight() / 2));
        });

        ghostLayer.setOnMouseClicked(e -> {
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

            ghostLayer.getChildren().remove(ghostImage);
            ghostLayer.setOnMouseMoved(null);
            ghostLayer.setOnMouseClicked(null);

            //Enable clicks on top layer
            //ghostLayer.setVisible(false);
            ghostLayer.setMouseTransparent(true);
            uiLayer.setMouseTransparent(false);
        });
    }

    private void showInfoWindow(Animal animal, double sceneX, double sceneY) {
        if (currentAnimalInfoPane != null) {
            closeInfoWindow(currentAnimalInfoPane);
        }

        System.out.println("Animal clicked");
        animal.setPaused(true);

        VBox newInfoWindow = new VBox();
        newInfoWindow.getStyleClass().add("info-window");
        newInfoWindow.setPrefSize(170, 70);

        Button sellAnimalBtn = new Button("Sell animal");
        sellAnimalBtn.getStyleClass().add("info-button");
        sellAnimalBtn.setOnAction(e -> gameEngine.sellAnimal(animal));;

        newInfoWindow.getChildren().add(sellAnimalBtn);

        newInfoWindow.setLayoutX(sceneX - 85);
        newInfoWindow.setLayoutY(sceneY - 170);

        ghostLayer.getChildren().add(newInfoWindow);
        infoWindow = newInfoWindow;

        ghostLayer.setVisible(true);

        currentAnimalInfoPane = animal;

        uiLayer.setOnMouseClicked(event -> {
            if (infoWindow != null && !newInfoWindow.getBoundsInParent().contains(event.getX(), event.getY())) {
                closeInfoWindow(animal);
            }
        });
    }

    private void closeInfoWindow(Animal animal) {
        if (infoWindow != null) {
            ghostLayer.getChildren().remove(infoWindow);
            infoWindow = null;
            animal.setPaused(false);
            currentAnimalInfoPane = null;
        }
    }



    public void updateDisplay(double time, int carnivores, int herbivores, int jeeps, int tourists, int ticketPrice, int  money){
        //STATS
        int days = (int) time / 24;
        int hours = (int) time % 24;

        gameTimeLabel.setText("Day: " + days + " Hour: " + hours);
        carnivoreCountLabel.setText(carnivores + "");
        herbivoreCountLabel.setText(herbivores + "");
        jeepCountLabel.setText(jeeps + "");
        touristCountLabel.setText(tourists + "");
        ticketPriceLabel.setText(ticketPrice + "");
        moneyLabel.setText(money + "");

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