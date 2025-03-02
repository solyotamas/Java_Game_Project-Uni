package testing;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.Random;

public class SmoothMap extends Pane {

    public SmoothMap(double width, double height) {
        setPrefSize(width, height); // ✅ Preferred size
        setMinSize(width, height);  // ✅ Prevents shrinking
        setMaxSize(width, height);  // ✅ Prevents expanding

        // ✅ Set background image
        this.setStyle("-fx-background-color: lightgreen;"); // ✅ Example: Green background

    }

    private void addPlant(String imagePath, double x, double y) {

        ImageView plant = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        plant.setFitWidth(50);  // Adjust size
        plant.setFitHeight(50);
        plant.setLayoutX(x);
        plant.setLayoutY(y);

        this.getChildren().add(plant); // Add to the world
    }

    public void placePlant(String imagePath) {
        this.setOnMouseClicked(event -> {
            double clickX = event.getX() - 25; // Get X coordinate of the click
            double clickY = event.getY() - 25; // Get Y coordinate of the click
            addPlant(imagePath, clickX, clickY); // Place the animal at clicked position
        });
    }

    public void addAnimal(String imagePath, double x, double y) {
        ImageView animal = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        animal.setFitWidth(60);
        animal.setFitHeight(60);
        animal.setLayoutX(x);
        animal.setLayoutY(y);

        this.getChildren().add(animal); // Add to world
        makeAnimalMove(animal); // Make it move randomly
    }

    public void makeAnimalMove(ImageView animal) {
        Random random = new Random();
        Timeline wander = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            double newX = random.nextDouble() * 800;  // Random X within the world
            double newY = random.nextDouble() * 600;  // Random Y within the world

            TranslateTransition move = new TranslateTransition(Duration.seconds(2), animal);
            move.setToX(newX);
            move.setToY(newY);
            move.play();
        }));
        wander.setCycleCount(Animation.INDEFINITE);
        wander.play();
    }
}
