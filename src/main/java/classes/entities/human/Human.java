package classes.entities.human;

import classes.entities.Direction;
import classes.entities.animals.AnimalState;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class Human extends Pane {

    protected double x;
    protected double y;
    protected double speed;
    protected double targetX;
    protected double targetY;

    protected double restingTimePassed = 0.0;

    protected HumanState state;
    protected HumanState previousState;

    //Images of the Animal, ui
    private Image spriteSheet;
    private Image walkDownImages[] = new Image[4];
    private Image walkLeftImages[] = new Image[4];
    private Image walkRightImages[] = new Image[4];
    private Image walkUpImages[] = new Image[4];
    int imageDelay = 0;
    int currentImage = 0;
    private ImageView imageView;
    private int frameWidth;
    private int frameHeight;
    protected Direction currentDirection = Direction.RIGHT;


    public Human(double x, double y, int frameWidth, int frameHeight, String imgUrl, double speed) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.speed = speed;

        //Animation pictures
        this.spriteSheet = new Image(getClass().getResource(imgUrl).toExternalForm());
        loadStaticDirectionImages();

        //UI
        imageView = new ImageView(walkRightImages[0]);
        imageView.setFitWidth(frameWidth * 0.8);
        imageView.setFitHeight(frameHeight * 0.8);
        getChildren().add(imageView);


        //x and y value is the feet
        //mouseclick is the middle
        setLayoutX(x - (frameWidth * 0.8 / 2.0));
        setLayoutY(y - (frameHeight * 0.8 / 2.0));
        this.x = x;
        this.y = y + (frameHeight * 0.8 / 2.0);

        state = HumanState.IDLE;

    }

    //=== UI
    private void loadStaticDirectionImages() {
        for (int i = 0; i < 4; i++) {
            walkDownImages[i] = new WritableImage(spriteSheet.getPixelReader(), i * frameWidth, 0 * frameHeight, frameWidth, frameHeight);
        }
        for (int i = 0; i < 4; i++) {
            walkLeftImages[i] = new WritableImage(spriteSheet.getPixelReader(), i * frameWidth, 1 * frameHeight, frameWidth, frameHeight);
        }
        for (int i = 0; i < 4; i++) {
            walkRightImages[i] = new WritableImage(spriteSheet.getPixelReader(), i * frameWidth, 2 * frameHeight, frameWidth, frameHeight);
        }
        for (int i = 0; i < 4; i++) {
            walkUpImages[i] = new WritableImage(spriteSheet.getPixelReader(), i * frameWidth, 3 * frameHeight, frameWidth, frameHeight);
        }
    }
    //=====

    //=== MOVEMENT
    public void move(Direction dir, double dx, double dy) {
        this.currentDirection = dir;
        this.x += dx;
        this.y += dy;

        //the UI element itself
        setLayoutX(getLayoutX() + dx);
        setLayoutY(getLayoutY() + dy);

        //updateDirectionImage();
        switch (currentDirection) {
            case UP -> {
                imageDelay++;
                if (imageDelay >= 5) {
                    currentImage = (currentImage + 1) % walkUpImages.length;
                    imageDelay = 0;
                }
                imageView.setImage(walkUpImages[currentImage]);
            }
            case DOWN -> {
                imageDelay++;
                if (imageDelay >= 5) {
                    currentImage = (currentImage + 1) % walkDownImages.length;
                    imageDelay = 0;
                }
                imageView.setImage(walkDownImages[currentImage]);
            }
            case RIGHT -> {
                imageDelay++;
                if (imageDelay >= 5) {
                    currentImage = (currentImage + 1) % walkRightImages.length;
                    imageDelay = 0;
                }
                imageView.setImage(walkRightImages[currentImage]);
            }
            case LEFT -> {
                // cycle through left frames
                imageDelay++;
                if (imageDelay >= 5) {
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

        // close enough -> Switch to rest
        if (Math.abs(dx) < 1 && Math.abs(dy) < 1) {
            if (state == HumanState.MOVING)
                transitionTo(HumanState.RESTING);
            if (state == HumanState.EXITING) {
                //System.out.println("Tourist reached exit: " + this);
                transitionTo(HumanState.LEFT);
            }
            return;
        }

        // normalize
        double dist = Math.hypot(dx, dy);
        double stepX = (dx / dist) * speed;
        double stepY = (dy / dist) * speed;

        // Determine direction for animation (dominant axis only)
        Direction dir;
        if (Math.abs(dx) > Math.abs(dy)) {
            dir = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        } else {
            dir = (dy > 0) ? Direction.DOWN : Direction.UP;
        }

        move(dir, stepX, stepY);  // ← move on both axes at once


    }

    public abstract void pickNewTarget();

    public void rest() {
        restingTimePassed += 0.05;
        if (restingTimePassed >= 10) {
            restingTimePassed = 0.0;
            state = HumanState.IDLE;
        }
    }
    //======

    //=== managing states
    public void transitionTo(HumanState newState) {
        if (newState == HumanState.PAUSED) {
            previousState = state;
        }
        this.state = newState;

    }

    public void resume() {
        if (state == HumanState.PAUSED && previousState != null) {
            transitionTo(previousState);
        }
    }
    //===


    //Getters, Setters
    public double getSpeed() {
        return this.speed;
    }

    public int getFrameWidth() {
        return this.frameWidth;
    }

    public int getFrameHeight() {
        return this.frameHeight;
    }

    public double getY() {
        return this.y;
    }

    public double getX() {
        return this.x;
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    public HumanState getState() {
        return state;
    }

}