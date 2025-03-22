package classes.entities.animals;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

public class Peacocktest extends Pane {
    private Image spriteSheet;

    private Image walkDownImages[] = new Image[4];
    private Image walkLeftImages[] = new Image[4];
    private Image walkRightImages[] = new Image[4];
    private Image walkUpImages[] = new Image[4];

    int imageDelay = 0;
    int currentImage= 0;

    private ImageView imageView;
    private final int frameWidth = 36;
    private final int frameHeight = 38;

    public Direction currentDirection = Direction.DOWN;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Peacocktest(double startX, double startY) {
        // Load the sprite sheet
        spriteSheet = new Image(getClass().getResource("/images/Peacock-folded-tail-Sheet.png").toExternalForm());
        loadStaticDirectionImages();

        // Set up ImageView with facing down image by default
        imageView = new ImageView(walkDownImages[0]);
        imageView.setFitWidth(frameWidth);
        imageView.setFitHeight(frameHeight);
        getChildren().add(imageView);

        setLayoutX(startX);
        setLayoutY(startY);
    }

    private void loadStaticDirectionImages() {


        for (int i = 0; i < 4; i++) {
            walkDownImages[i] = new WritableImage(spriteSheet.getPixelReader(), 0 * frameWidth, i * frameHeight, frameWidth, frameHeight);
        }
        for (int i = 0; i < 4; i++) {
            walkRightImages[i] = new WritableImage(spriteSheet.getPixelReader(), 1 * frameWidth, i * frameHeight, frameWidth, frameHeight);
        }
        for (int i = 0; i < 4; i++) {
            walkUpImages[i] = new WritableImage(spriteSheet.getPixelReader(), 2 * frameWidth, i * frameHeight, frameWidth, frameHeight);
        }
        for (int i = 0; i < 4; i++) {
            walkLeftImages[i] = new WritableImage(spriteSheet.getPixelReader(), 3 * frameWidth, i * frameHeight, frameWidth, frameHeight);
        }
    }

    public void move(Direction dir, double dx, double dy) {
        this.currentDirection = dir;
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

    private void updateDirectionImage() {


    }
}