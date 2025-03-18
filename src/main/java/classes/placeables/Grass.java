package classes.placeables;

public class Grass extends Plant {
    private static final int WIDTH_IN_TILES = 1;
    private static final int HEIGHT_IN_TILES = 1;


    public Grass(int x, int y) {
        super(x, y, HEIGHT_IN_TILES, WIDTH_IN_TILES, "/images/grass.png");
    }
}