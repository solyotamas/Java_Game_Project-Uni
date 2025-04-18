package classes.landforms;

import javafx.scene.image.Image;

import java.util.Objects;


public class Road extends Landform {
    private static final double depth = Double.MIN_VALUE + 1;
    public static final Image[] roadImages = new Image[16];

    public static final int WIDTH_IN_TILES = 1;
    public static final int HEIGHT_IN_TILES = 1;

    private static final int price = 200;

    public Road(double x, double y) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES,roadImages[0], depth, price);
    }

    public Road(double x, double y, double placeHolder) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES,roadImages[0], depth, price);
    }

    public Road(double x, double y, double placeHolder1, Image placeHolder2) {
        super(x, y, WIDTH_IN_TILES, HEIGHT_IN_TILES, roadImages[0], depth, price);
    }


    static {
        for (int i = 0; i < 16; i++) {
            roadImages[i] = new Image(Road.class.getResource("/images/roads/road" + i + ".png").toExternalForm());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Road road = (Road) o;
        return x == road.x && y == road.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}