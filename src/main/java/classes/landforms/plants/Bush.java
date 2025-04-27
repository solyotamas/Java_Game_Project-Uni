package classes.landforms.plants;

import javafx.scene.image.Image;

import java.util.Random;

public class Bush extends Plant {
    private static final Image[] bushImages = new Image[3];
    private static final Random rand = new Random();

    public static final int WIDTH_IN_TILES = 1;
    public static final int HEIGHT_IN_TILES = 1;

    private static final int price = 500;

    public Bush(double x, double y, double depth) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES, getRandomBushImage(), depth, price);
    }

    public Bush(double x, double y, double depth, Image img) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES, img, depth, price);
    }

    public static Image getRandomBushImage() {
        return bushImages[rand.nextInt(bushImages.length)];
    }

    static {
        for (int i = 0; i < 3; i++) {
            bushImages[i] = new Image(Tree.class.getResource("/images/bush" + (i+1) + ".png").toExternalForm());
        }
    }
}