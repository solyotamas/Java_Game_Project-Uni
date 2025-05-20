package classes.landforms.plants;

import javafx.scene.image.Image;

import java.util.Random;

public class Tree extends Plant {
    private static final Image[] treeImages = new Image[2];
    private static final Random rand = new Random();

    public static final int WIDTH_IN_TILES = 2;
    public static final int HEIGHT_IN_TILES = 2;

    private static final int price = 800;
    private static final int nutrition = 200;

    public Tree(double x, double y, double depth) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES, getRandomTreeImage(), depth, price, nutrition);
    }

    public Tree(double x, double y, double depth, Image img) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES, img, depth, price, nutrition);
    }

    public static Image getRandomTreeImage() {
        return treeImages[rand.nextInt(treeImages.length)];
    }

    static {
        for (int i = 0; i < 2; i++) {
            treeImages[i] = new Image(Tree.class.getResource("/images/tree" + (i+1) + ".png").toExternalForm());
        }
    }
}