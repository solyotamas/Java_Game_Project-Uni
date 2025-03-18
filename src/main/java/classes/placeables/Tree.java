package classes.placeables;

import java.util.Random;

public class Tree extends Plant {
    private static final int WIDTH_IN_TILES = 1;
    private static final int HEIGHT_IN_TILES = 2;

    public Tree(int x, int y) {
        super(x, y, HEIGHT_IN_TILES, WIDTH_IN_TILES, getRandomTreeImage());
    }

    private static String getRandomTreeImage() {
        Random rand = new Random();
        int ran = rand.nextInt(2) + 1;
        return "/images/tree" + ran + ".png";
    }
}