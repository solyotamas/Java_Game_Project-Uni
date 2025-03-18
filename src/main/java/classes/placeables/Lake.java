package classes.placeables;

public class Lake extends Plant{
    private static final int WIDTH_IN_TILES = 2;
    private static final int HEIGHT_IN_TILES = 1;


    public Lake(int x, int y) {
        super(x, y, HEIGHT_IN_TILES, WIDTH_IN_TILES,"/images/lake.png" );
    }

}
