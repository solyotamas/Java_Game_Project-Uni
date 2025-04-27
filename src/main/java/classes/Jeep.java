package classes;

import classes.entities.Direction;
import classes.landforms.Road;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

//todo:
//  - a kép útvonal még lehet rossz
//  - a többit kb a Placeableből másoltam
//  - a radiust majd ki kell találni
//  - valszeg kell majd egy ilyen irány field hogy éppen merre megyünk mert a képed majd forgatni kell


//mire kell az absrtact?
public class Jeep extends Pane {
    public double x, y;
    private double depth;
    protected ImageView picture;
    private int frameWidth;
    private int frameHeight;
    public int radius;
    public int happyBonus;
    public int speciesSeen;
    private Direction currentDirection;

    private WritableImage[] jeepRight;
    private WritableImage[] jeepLeft;
    private WritableImage[] jeepUp;
    private WritableImage[] jeepDown;
    private Image spriteSheet;

    private int currentFrame = 0;
    private int frameDelay = 0;

    public Jeep(int x, int y) {
        this.x = x;
        this.y = y;
        this.frameWidth = 37;
        this.frameHeight = 30;
        this.radius = 0; //todo
        this.happyBonus = 0;
        this.speciesSeen = 0;
        this.currentDirection = Direction.RIGHT;

        spriteSheet = new Image(getClass().getResource("/images/jeep-spritesheet.png").toExternalForm());
        loadFrames();

        this.picture = new ImageView(jeepRight[0]);
        picture.setPreserveRatio(true);
        picture.setFitHeight(30);
        this.depth = y + picture.getFitHeight();

        this.getChildren().add(picture);

        setLayoutX(x);
        setLayoutY(y);
    }

    private void loadFrames() {
        jeepDown = new WritableImage[2];
        jeepLeft = new WritableImage[2];
        jeepRight = new WritableImage[2];
        jeepUp = new WritableImage[2];

        // DOWN és UP: 32x47
        for (int i = 0; i < 2; i++) {
            jeepUp[i] = new WritableImage(spriteSheet.getPixelReader(), 0, i * 52, 54, 52);
            jeepDown[i] = new WritableImage(spriteSheet.getPixelReader(), 54, i * 52, 54, 52);
        }

        // LEFT és RIGHT: 67x30
        for (int i = 0; i < 2; i++) {
            jeepLeft[i] = new WritableImage(spriteSheet.getPixelReader(), 108, i * 52, 103, 52);
            jeepRight[i] = new WritableImage(spriteSheet.getPixelReader(), 211, i * 52, 103, 52);
        }
    }

    private void updateImage(Direction direction) {
        if (this.currentDirection != direction) {
            this.currentDirection = direction;
            currentFrame = 0;
            frameDelay = 0;
        }

        frameDelay++;
        if (frameDelay >= 10) {
            currentFrame = (currentFrame + 1) % 2;
            frameDelay = 0;
        }

        switch (currentDirection) {
            case RIGHT -> picture.setImage(jeepRight[currentFrame]);
            case LEFT -> picture.setImage(jeepLeft[currentFrame]);
            case UP -> picture.setImage(jeepUp[currentFrame]);
            case DOWN -> picture.setImage(jeepDown[currentFrame]);
        }
    }

    public double getDepth() {
        return this.depth;
    }
}