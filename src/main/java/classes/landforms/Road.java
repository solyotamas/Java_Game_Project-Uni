package classes.landforms;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Road extends Landform {
    private static final double depth = Double.MIN_VALUE + 1;
    public static final Image[] roadImages = new Image[16];

    public static final int WIDTH_IN_TILES = 1;
    public static final int HEIGHT_IN_TILES = 1;

    public Road(double x, double y) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES,roadImages[0], depth);
    }

    public Road(double x, double y, double placeHolder) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES,roadImages[0], depth);
    }

    public Road(double x, double y, double placeHolder1, Image placeHolder2) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES, roadImages[0], depth);
    }


    static {
        for (int i = 0; i < 16; i++) {
            roadImages[i] = new Image(Road.class.getResource("/images/roads/road" + i + ".png").toExternalForm());
        }
    }



}