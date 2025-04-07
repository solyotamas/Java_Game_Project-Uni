package classes.controllers;

import classes.Difficulty;
import classes.entities.animals.*;
import classes.entities.animals.carnivores.*;
import classes.entities.animals.herbivores.*;
import classes.entities.human.*;
import classes.game.GameEngine;
import classes.landforms.*;
import classes.landforms.plants.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.image.ImageView;


public class GameController {
    private GameEngine gameEngine;
    private Difficulty difficulty;

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
    private AnchorPane saveOverlay;

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
        //TODO somehow remove runLater but still get the right difficulty and not null
        Platform.runLater(() -> {
            this.gameEngine = new GameEngine(this, difficulty, terrainLayer, uiLayer, ghostLayer);
            gameEngine.gameLoop();
        });
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @FXML
    public void speedGameToDay(){

    }
    @FXML
    public void speedGameToHour(){

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
                    remainingRoads[0]--;

                    if (isRoad && placedLandform instanceof Road road) {
                        gameEngine.roads.add(road); // Itt add hozzá közvetlenül!
                    }
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

    public void buyBush() {
        buyLandform(Bush.class, Bush.getRandomBushImage());
    }
    public void buyTree() {
        buyLandform(Tree.class, Tree.getRandomTreeImage());
    }
    public void buyLake() {
        buyLandform(Lake.class, Lake.lakePicture);
    }
    public void buyGrass() {
        buyLandform(Grass.class, Grass.grassPicture);
    }
    public void buyRoad() {
        buyLandform(Road.class, Road.roadImages[0]);
    }
    public void buyJeep() {{
        gameEngine.buyJeep();
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

                System.out.println("Added animal at " + placeX + ", " + placeY);
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

    public void buyElephant(){
        buyAnimal(Elephant.class, "/images/elephant.png");
    }
    public void buyRhino(){
        buyAnimal(Rhino.class, "/images/rhino.png");
    }
    public void buyHippo(){
        buyAnimal(Hippo.class, "/images/hippo.png");
    }
    public void buyBuffalo(){
        buyAnimal(Buffalo.class, "/images/buffalo.png");
    }
    public void buyZebra(){
        buyAnimal(Zebra.class, "/images/zebra.png");
    }
    public void buyKangaroo(){
        buyAnimal(Kangaroo.class, "/images/kangaroo.png");
    }
    public void buyTurtle(){
        buyAnimal(Turtle.class, "/images/turtle.png");
    }
    public void buyLion(){
        buyAnimal(Lion.class, "/images/lion.png");
    }
    public void buyTiger(){
        buyAnimal(Tiger.class, "/images/tiger.png");
    }
    public void buyPanther(){
        buyAnimal(Panther.class, "/images/panther.png");
    }
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


    //Stat updating on labels
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

    //UI OVERLAYS
    //Market appear, disappear
    public void happens() {
        shopPane.setVisible(true);
    }

    public void closeShopPane(){
        shopPane.setVisible(false);
    }

    //Info panels
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

    //Save screen pane
    public void showSaveOverlay() {
        saveOverlay.setVisible(true);
    }

    public void hideSaveOverlay() {
        saveOverlay.setVisible(false);
    }

    //SWITCHING BACK TO MAIN
    public void switchToMain(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/main_screen.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}