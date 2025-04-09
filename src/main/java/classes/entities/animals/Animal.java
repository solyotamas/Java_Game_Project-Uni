package classes.entities.animals;

import classes.entities.Direction;
import classes.entities.human.Ranger;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public abstract class Animal extends Pane {


    /*
    private Landform target;
    private int appetite;
    private boolean hungry;
    private boolean thirsty;
    private ArrayList<Landform> consumes;
    private Herd herd;*/

    private int price;
    private int age;
    private double x;
    private double y;
    protected double speed;
    protected double targetX;
    protected double targetY;
    private double restingTimePassed = 0.0;
    private double restDuration = 15.0;
    private boolean resting = false;
    private boolean paused = false;

    //Images of the Animal, ui
    private Image spriteSheet;
    private Image walkDownImages[] = new Image[3];
    private Image walkLeftImages[] = new Image[3];
    private Image walkRightImages[] = new Image[3];
    private Image walkUpImages[] = new Image[3];
    int imageDelay = 0;
    int currentImage = 0;
    private ImageView imageView;
    private int frameWidth;
    private int frameHeight;
    protected Direction currentDirection = Direction.RIGHT;

    private Pane infoWindow;
    private static Animal currentAnimalWithInfoWindow = null;

    public Animal(double x, double y, int frameWidth, int frameHeight, String imgUrl, double speed, int price)  {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.speed = speed;
        this.price = price;

        //Animation pictures
        this.spriteSheet = new Image(getClass().getResource(imgUrl).toExternalForm());
        loadStaticDirectionImages();

        //UI
        imageView = new ImageView(walkDownImages[0]);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        getChildren().add(imageView);

        //the picture will appear where the user clicked but the x and y coordinates are its feet for dynamic depth
        setLayoutX(x - (50 / 2.0));
        setLayoutY(y - (50 / 2.0));
        this.x = x;
        this.y = y + (50 / 2.0);

        pickNewTarget(1920,930);
    }

    private void loadStaticDirectionImages() {
        for (int i = 0; i < 3; i++) {
            walkDownImages[i] = new WritableImage(spriteSheet.getPixelReader(), i * frameWidth, 0 * frameHeight, frameWidth, frameHeight);
        }
        for (int i = 0; i < 3; i++) {
            walkLeftImages[i] = new WritableImage(spriteSheet.getPixelReader(), i * frameWidth, 1 * frameHeight, frameWidth, frameHeight);
        }
        for (int i = 0; i < 3; i++) {
            walkRightImages[i] = new WritableImage(spriteSheet.getPixelReader(), i * frameWidth, 2 * frameHeight, frameWidth, frameHeight);
        }
        for (int i = 0; i < 3; i++) {
            walkUpImages[i] = new WritableImage(spriteSheet.getPixelReader(), i * frameWidth, 3 * frameHeight, frameWidth, frameHeight);
        }
    }

    public void move(Direction dir, double dx, double dy) {
        this.currentDirection = dir;
        this.x += dx; this.y += dy;

        //the UI element itself
        setLayoutX(getLayoutX() + dx);
        setLayoutY(getLayoutY() + dy);

        //updateDirectionImage();
        switch (currentDirection) {
            case UP -> {
                imageDelay++;
                if (imageDelay >= 10) {
                    currentImage = (currentImage + 1) % walkUpImages.length;
                    imageDelay = 0;
                }
                imageView.setImage(walkUpImages[currentImage]);
            }
            case DOWN -> {
                imageDelay++;
                if (imageDelay >= 10) {
                    currentImage = (currentImage + 1) % walkDownImages.length;
                    imageDelay = 0;
                }
                imageView.setImage(walkDownImages[currentImage]);
            }
            case RIGHT -> {
                imageDelay++;
                if (imageDelay >= 10) {
                    currentImage = (currentImage + 1) % walkRightImages.length;
                    imageDelay = 0;
                }
                imageView.setImage(walkRightImages[currentImage]);
            }
            case LEFT -> {
                // cycle through left frames
                imageDelay++;
                if (imageDelay >= 10) {
                    currentImage = (currentImage + 1) % walkLeftImages.length;
                    imageDelay = 0;
                }
                imageView.setImage(walkLeftImages[currentImage]);
            }
        }
    }
    public void moveTowardsTarget() {
        if (paused) {
            return;
        }

        double dx = targetX - x;
        double dy = targetY - y;

        if (Math.abs(dx) < 5 && Math.abs(dy) < 5) {
            resting = true;
            return;
        }

        // Prioritize X-axis movement first
        if ((Math.abs(dx) > 1)) {
            if (dx > 0) {
                move(Direction.RIGHT, speed, 0);
            } else {
                move(Direction.LEFT, -speed, 0);
            }
        }
        // Only move on Y-axis once close enough in X
        else if (Math.abs(dy) > 1) {
            if (dy > 0) {
                move(Direction.DOWN, 0, speed);
            } else {
                move(Direction.UP, 0, -speed);
            }
        }
    }

    public void rest(double mapWidth, double mapHeight) {
        restingTimePassed += 0.05; // updateAnimalPositions() is 50ms

        if (restingTimePassed >= restDuration) {
            resting = false;
            restingTimePassed = 0.0;
            pickNewTarget(mapWidth, mapHeight);
        }
    }

    public void pickNewTarget(double mapWidth, double mapHeight) {
        double marginX = 200;
        double marginY = 50;
        this.targetX = marginX + Math.random() * (mapWidth - 2 * marginX);
        this.targetY = marginY + Math.random() * (mapHeight - 2 * marginY);
    }

    public void sellAnimal(){
        System.out.println(this.getClass() + " sold");
        //TODO sell animal
    }


    //Getters, Setters
    public double getSpeed(){
        return this.speed;
    }
    public boolean getResting(){
        return this.resting;
    }
    public int getPrice() { return this.price; }
    public int getFrameWidth(){
        return this.frameWidth;
    }
    public int getFrameHeight(){
        return this.frameHeight;
    }
    public double getY(){
        return this.y;
    }
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

}