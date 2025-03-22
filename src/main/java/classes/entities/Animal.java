package classes.entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import java.util.Random;

public abstract class Animal extends Pane {


    /*
    private Landform target;
    private int appetite;
    private boolean hungry;
    private boolean thirsty;
    private ArrayList<Landform> consumes;
    private Herd herd;*/

    private int age;
    private double x;
    private double y;
    protected double speed;
    protected double targetX = 1200;
    protected double targetY = 600;

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
    protected Direction currentDirection = Direction.DOWN;


    public Animal(double x, double y, int frameWidth, int frameHeight, String imgUrl, double speed)  {
        this.x = x;
        this.y = y;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.speed = speed;

        //Animation pictures
        this.spriteSheet = new Image(getClass().getResource(imgUrl).toExternalForm());
        loadStaticDirectionImages();

        //UI
        imageView = new ImageView(walkDownImages[0]);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        getChildren().add(imageView);

        setLayoutX(x);
        setLayoutY(y);
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


        double dx = targetX - x;
        double dy = targetY - y;

        // If close enough, pick a new target
        if (Math.abs(dx) == 0 && Math.abs(dy) == 0) {
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


    //Getters, Setters
    public Direction getCurrentDirection(){
        return this.currentDirection;
    }
    public void setCurrentDirection(Direction direction){
        this.currentDirection = direction;
    }
    public double getSpeed(){
        return this.speed;
    }



}