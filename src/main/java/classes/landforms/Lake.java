package classes.landforms;

import javafx.scene.image.Image;

public class Lake extends Landform {
    private static final double depth = 0.0;
    public static final Image lakePicture = new Image(Road.class.getResource("/images/lake.png").toExternalForm());

    public static final int WIDTH_IN_TILES = 4;
    public static final int HEIGHT_IN_TILES = 2;

    public Lake(double x, double y) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES, lakePicture, depth);
    }

    public Lake(double x, double y, double placeHolder) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES, lakePicture, depth);
    }
    public Lake(double x, double y, double placeHolder, Image img) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES, img, depth);
    }





}