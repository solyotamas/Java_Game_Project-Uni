package classes.entities.human;

import classes.entities.Direction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

public abstract class Human extends Pane {

    private double x;
    private double y;
    protected double speed;
    protected double targetX;
    protected double targetY;
    private boolean paused = false;

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


    public Human(double x, double y, int frameWidth, int frameHeight, String imgUrl, double speed)  {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.speed = speed;

        //Animation pictures
        this.spriteSheet = new Image(getClass().getResource(imgUrl).toExternalForm());
        loadStaticDirectionImages();

        //UI
        imageView = new ImageView(walkRightImages[0]);
        imageView.setFitWidth(frameWidth * 0.4);
        imageView.setFitHeight(frameHeight * 0.4);
        getChildren().add(imageView);

        //the picture will appear where the user clicked but the x and y coordinates are its feet for dynamic depth
        setLayoutX(x - (frameWidth * 0.8 / 2.0));
        setLayoutY(y - (frameHeight * 0.8 / 2.0));
        this.x = x;
        this.y = y + (frameHeight * 0.8 / 2.0);

        pickNewTarget(1920,930);
    }

    private void loadStaticDirectionImages() {
        for (int i = 0; i < 4; i++) {
            walkDownImages[i] = new WritableImage(spriteSheet.getPixelReader(), i * frameWidth, 0 * frameHeight, frameWidth, frameHeight);
        }
        for (int i = 0; i < 4; i++) {
            walkRightImages[i] = new WritableImage(spriteSheet.getPixelReader(), i * frameWidth, 1 * frameHeight, frameWidth, frameHeight);
        }
        for (int i = 0; i < 4; i++) {
            walkLeftImages[i] = new WritableImage(spriteSheet.getPixelReader(), i * frameWidth, 2 * frameHeight, frameWidth, frameHeight);
        }
        for (int i = 0; i < 4; i++) {
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
        if (paused) return;

        double dx = targetX - x;
        double dy = targetY - y;

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

        if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) {
            pickNewTarget(1920, 930);  // Generate a new target once reached
        }
    }

    public void pickNewTarget(double mapWidth, double mapHeight) {
        double marginX = 200;
        double marginY = 50;
        this.targetX = marginX + Math.random() * (mapWidth - 2 * marginX);
        this.targetY = marginY + Math.random() * (mapHeight - 2 * marginY);
    }


    //Getters, Setters
    public double getSpeed(){
        return this.speed;
    }
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