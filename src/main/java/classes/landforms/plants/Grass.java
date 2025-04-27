package classes.landforms.plants;

import classes.landforms.Road;
import javafx.scene.image.Image;

public class Grass extends Plant {
    public static final Image grassPicture = new Image(Road.class.getResource("/images/grass.png").toExternalForm());

    public static final int WIDTH_IN_TILES = 1;
    public static final int HEIGHT_IN_TILES = 1;

    private static final int price = 300;

    public Grass(double x, double y, double depth) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES, grassPicture, depth, price);
    }

    public Grass(double x, double y, double depth, Image img) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES, img, depth, price);
    }
}