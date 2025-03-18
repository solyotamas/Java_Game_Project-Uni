package classes.placeables;

import java.util.Random;

public class Bush extends Plant {
    private static final int WIDTH_IN_TILES = 1;
    private static final int HEIGHT_IN_TILES = 1;

    public Bush(int x, int y) {
        super(x, y, HEIGHT_IN_TILES, WIDTH_IN_TILES,getRandomBushImage());
    }

    private static String getRandomBushImage() {
        Random rand = new Random();
        int ran = rand.nextInt(2) + 1;
        return "/images/bush" + ran + ".png";
    }
}