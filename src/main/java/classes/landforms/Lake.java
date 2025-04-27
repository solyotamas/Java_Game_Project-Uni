package classes.landforms;

import javafx.scene.image.Image;

public class Lake extends Landform {
    private static final double depth = 0.0;
    public static final Image lakePicture = new Image(Road.class.getResource("/images/lake.png").toExternalForm());

    public static final int WIDTH_IN_TILES = 4;
    public static final int HEIGHT_IN_TILES = 2;

    private static final int price = 2500;

    public Lake(double x, double y) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES, lakePicture, depth, price);
    }

    public Lake(double x, double y, double placeHolder) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES, lakePicture, depth, price);
    }
    public Lake(double x, double y, double placeHolder, Image img) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES, img, depth, price);
    }





}