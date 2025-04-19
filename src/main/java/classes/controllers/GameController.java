package classes.controllers;

import classes.Difficulty;
import classes.entities.additions.InfoWindowAnimal;
import classes.entities.additions.InfoWindowRanger;
import classes.entities.animals.Animal;
import classes.entities.animals.AnimalState;
import classes.entities.animals.Herbivore;
import classes.entities.human.HumanState;
import classes.entities.human.Tourist;
import classes.entities.animals.carnivores.Lion;
import classes.entities.animals.carnivores.Panther;
import classes.entities.animals.carnivores.Tiger;
import classes.entities.animals.carnivores.Vulture;
import classes.entities.animals.herbivores.*;
import classes.entities.human.Ranger;
import classes.game.GameEngine;

import classes.landforms.Lake;
import classes.landforms.Landform;
import classes.landforms.Road;
import classes.landforms.plants.Bush;
import classes.landforms.plants.Grass;
import classes.landforms.plants.Plant;
import classes.landforms.plants.Tree;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Random;

import javafx.scene.image.ImageView;


public class GameController {
    private GameEngine gameEngine;
    private Difficulty difficulty;

    //info panel
    private InfoWindowAnimal currentInfoWindowAnimal = null;
    private InfoWindowRanger currentInfoWindowRanger = null;

    //random
    private final Random rand = new Random();

    //stats
    private final int TILE_SIZE = 30;
    private final int SCREEN_WIDTH = 1920;
    private final int SCREEN_HEIGHT = 930;
    private final int TOURIST_SECTION = 150;
    private final int HEADER_FOOTER = 75;

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
    @FXML
    private AnchorPane losePane;
    @FXML
    private AnchorPane winPane;

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


    public void startGame() {
        this.gameEngine = new GameEngine(this, difficulty, terrainLayer, uiLayer);
        System.out.println(difficulty);
        gameEngine.gameLoop();
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

    // ==== LANDFORMS
    private void buyLandform(Class<? extends Landform> landformClass, Image chosen) {
        closeShopPane();


        boolean isRoad = Road.class.isAssignableFrom(landformClass); // If road, remainingPlacableTiles is 10, otherwise 1
        int[] remainingPlacableTiles = isRoad ? new int[]{10} : new int[]{1}; //Counter in array because you cant change primitive variables in lambda

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
        ghostLayer.setVisible(true);
        ghostLayer.setMouseTransparent(false);
        ghostLayer.setOnMouseMoved(e -> {
            double minX = 150;
            double minY = 0;
            double maxX = terrainLayer.getWidth() - ghostImage.getFitWidth() - 150;
            double maxY = terrainLayer.getHeight() - ghostImage.getFitHeight();

            double clampedX = Math.max(minX, Math.min(e.getX(), maxX));
            double clampedY = Math.max(minY, Math.min(e.getY(), maxY));

            double snapX = Math.floor(clampedX / TILE_SIZE) * TILE_SIZE;
            double snapY = Math.floor(clampedY / TILE_SIZE) * TILE_SIZE;
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
                        gameEngine.buyPlant((Plant) placedLandform);
                    } else if (placedLandform instanceof Lake) {
                        gameEngine.buyLake((Lake) placedLandform);
                    }
                    remainingPlacableTiles[0]--;

                    if (isRoad) {
                        gameEngine.buyRoad((Road) placedLandform);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //If road, place 10
            if (!isRoad || remainingPlacableTiles[0] <= 0) {

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
    // =====


    // ==== ANIMALS
    private void buyAnimal(Class<? extends Animal> animalClass, String imagePath) {
        closeShopPane();
        ghostLayer.setVisible(true);
        ghostLayer.setMouseTransparent(false);


        // Create a temporary instance just to get size info
        Animal tempAnimal = null;
        try {
            tempAnimal = animalClass.getDeclaredConstructor(double.class, double.class)
                    .newInstance(0.0, 0.0);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        ImageView ghostImage = tempAnimal.getImageView();
        ghostImage.setOpacity(0.5);
        ghostLayer.getChildren().add(ghostImage);

        ghostLayer.setOnMouseMoved(e -> {
            double imgWidth = ghostImage.getFitWidth();
            double imgHeight = ghostImage.getFitHeight();

            // Calculate edges of allowed area based on center-positioning
            double minX = TOURIST_SECTION + imgWidth / 2;
            double maxX = SCREEN_WIDTH - TOURIST_SECTION - imgWidth / 2;
            double minY = imgHeight / 2;
            double maxY = SCREEN_HEIGHT - imgHeight / 2;

            // Clamp mouse position
            double clampedX = Math.max(minX, Math.min(e.getX(), maxX));
            double clampedY = Math.max(minY, Math.min(e.getY(), maxY));

            // Position the ghost image centered on the clamped coordinates
            ghostImage.setLayoutX(clampedX - imgWidth / 2);
            ghostImage.setLayoutY(clampedY - imgHeight / 2);

        });

        ghostLayer.setOnMouseClicked(e -> {
            e.consume();

            try {
                double placeX = e.getX();
                double placeY = e.getY();
                Animal animalInstance = animalClass
                        .getDeclaredConstructor(double.class, double.class)
                        .newInstance(placeX, placeY);

                //click
                if(gameEngine.haveEnoughMoneyForAnimal(animalInstance) && canPlaceAnimal(animalInstance, placeX, placeY)){

                    Platform.runLater(() -> {
                        animalInstance.setOnMouseClicked(this::handleAnimalClicked);
                    });

                    gameEngine.buyAnimal(animalInstance);
                    uiLayer.getChildren().add(animalInstance);
                }

                //System.out.println("Added animal at " + placeX + ", " + placeY);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            ghostLayer.getChildren().remove(ghostImage);
            ghostLayer.setOnMouseMoved(null);
            ghostLayer.setOnMouseClicked(null);

            ghostLayer.setVisible(false);
            ghostLayer.setMouseTransparent(true);

        });
    }

    private boolean canPlaceAnimal(Animal animalInstance, double placeX, double placeY) {

        double imgWidth = animalInstance.getImageView().getFitWidth();
        double imgHeight = animalInstance.getImageView().getFitHeight();


        double leftX = placeX - imgWidth / 2.0;
        double rightX = placeX + imgWidth / 2.0;
        double topY = placeY - imgHeight / 2.0;
        double bottomY = placeY + imgHeight / 2.0;

        return (
                leftX >= TOURIST_SECTION &&
                rightX <= SCREEN_WIDTH - TOURIST_SECTION &&
                topY >= 0 &&
                bottomY <= SCREEN_HEIGHT
        );
    }
    @FXML
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
    public void removeHerbivore(Herbivore herbivore){
        uiLayer.getChildren().remove(herbivore);
    }
    // =====


    // ==== RANGERS
    @FXML
    private void buyRanger() {
        closeShopPane();

        ghostLayer.setVisible(true);
        ghostLayer.setMouseTransparent(false);

        Image rangerImage = new Image(getClass().getResource("/images/ranger.png").toExternalForm());
        ImageView ghostImage = new ImageView(rangerImage);
        ghostImage.setOpacity(0.5);
        ghostImage.setMouseTransparent(true);
        ghostImage.setFitWidth(28);
        ghostImage.setPreserveRatio(true);

        ghostLayer.getChildren().add(ghostImage);
        uiLayer.setMouseTransparent(true); // Disable clicks on uiLayer

        ghostLayer.setOnMouseMoved(e -> {
            ghostImage.setLayoutX(e.getX() - (ghostImage.getFitWidth() / 2));
            ghostImage.setLayoutY(e.getY() - (ghostImage.getFitHeight() / 2) - 18);
        });

        ghostLayer.setOnMouseClicked(e -> {
            e.consume();

            try {
                double placeX = e.getX();
                double placeY = e.getY();

                Ranger rangerInstance = new Ranger(placeX, placeY);

                if (gameEngine.haveEnoughMoneyForRanger(rangerInstance) &&
                        canPlaceRanger(rangerInstance, placeX, placeY)) {

                    Platform.runLater(() -> {
                        rangerInstance.setOnMouseClicked(this::handleRangerClicked);
                    });

                    gameEngine.buyRanger(rangerInstance);
                    uiLayer.getChildren().add(rangerInstance);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            ghostLayer.getChildren().remove(ghostImage);
            ghostLayer.setOnMouseMoved(null);
            ghostLayer.setOnMouseClicked(null);

            ghostLayer.setVisible(false);
            ghostLayer.setMouseTransparent(true);
            uiLayer.setMouseTransparent(false);
        });
    }

    private boolean canPlaceRanger(Ranger rangerInstance, double placeX, double placeY) {

        double imgWidth = rangerInstance.getImageView().getFitWidth();
        double imgHeight = rangerInstance.getImageView().getFitHeight();

        double leftX = placeX - imgWidth / 2.0;
        double rightX = placeX + imgWidth / 2.0;
        double topY = placeY - imgHeight / 2.0;
        double bottomY = placeY + imgHeight / 2.0;

        return (
                leftX >= TOURIST_SECTION &&
                        rightX <= SCREEN_WIDTH - TOURIST_SECTION &&
                        topY >= 0 &&
                        bottomY <= SCREEN_HEIGHT
        );
    }

    // =====


    // ==== INFO WINDOWS
    private void handleAnimalClicked(MouseEvent event) {
        if (currentInfoWindowAnimal != null || currentInfoWindowRanger != null)
            return;

        event.consume();

        Animal clickedAnimal = (Animal) event.getSource();
        clickedAnimal.transitionTo(AnimalState.PAUSED);

        currentInfoWindowAnimal = new InfoWindowAnimal(
                clickedAnimal,
                () -> {
                    // Sell action
                    gameEngine.sellAnimal(clickedAnimal);
                    uiLayer.getChildren().remove(clickedAnimal);

                    closeAnimalWindow(clickedAnimal);
                },
                () -> {
                    // Close action
                    closeAnimalWindow(clickedAnimal);
                }
        );

        uiLayer.getChildren().add(currentInfoWindowAnimal);
    }
    private void closeAnimalWindow(Animal animal) {
        if (currentInfoWindowAnimal != null) {
            uiLayer.getChildren().remove(currentInfoWindowAnimal);
            currentInfoWindowAnimal = null;
        }
        animal.resume();
    }
    private void handleRangerClicked(MouseEvent event) {
        if (currentInfoWindowAnimal != null || currentInfoWindowRanger != null)
            return;

        event.consume();

        Ranger clickedRanger = (Ranger) event.getSource();
        clickedRanger.transitionTo(HumanState.PAUSED);

        currentInfoWindowRanger = new InfoWindowRanger(
                clickedRanger,
                () -> {
                    // Unemploy action
                    gameEngine.unemployRanger(clickedRanger);
                    uiLayer.getChildren().remove(clickedRanger);

                    closeRangerWindow(clickedRanger);
                },
                () -> {
                    //...
                },
                () -> {
                    // Close action
                    closeRangerWindow(clickedRanger);
                }
        );

        uiLayer.getChildren().add(currentInfoWindowRanger);
    }
    private void closeRangerWindow(Ranger ranger) {
        if (currentInfoWindowRanger != null) {
            uiLayer.getChildren().remove(currentInfoWindowRanger);
            currentInfoWindowRanger = null;
        }
        ranger.resume();
    }
    // =====

    // ==== TOURISTS
    public Tourist spawnTourist(){
        Tourist template = new Tourist(0,0,0);

        //which side - 0 left, 1 right

        int coinFlip = rand.nextBoolean() ? 0 : 1;
        double x, y;
        Tourist tourist;
        if(coinFlip == 0){
            x = template.getImageView().getFitWidth() / 2;
            y = 31.0 * TILE_SIZE / 2. + template.getImageView().getFitHeight() / 2.;
            tourist = new Tourist(x,y,0);
        }
        else{
            x = 64 * TILE_SIZE - template.getImageView().getFitWidth() / 2;
            y = 31.0 * TILE_SIZE / 2. + template.getImageView().getFitHeight() / 2.;
            tourist = new Tourist(x,y,1);
        }

        uiLayer.getChildren().add(tourist);
        return tourist;

    }
    public void removeTourist(Tourist tourist){
        uiLayer.getChildren().remove(tourist);
    }
    // =====


    // ==== UI HANDLING
    //Display
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

    //Market appear, disappear
    public void openShopPane() {
        shopPane.setVisible(true);
    }
    public void closeShopPane(){
        shopPane.setVisible(false);
    }

    //Lose appear
    public void openLosePane() {
        losePane.setVisible(true);
    }

    //Win appear
    public void openWinPane() {
        winPane.setVisible(true);
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
    // =====
}