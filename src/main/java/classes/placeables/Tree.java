package classes.placeables;

import java.util.Random;

public class Tree extends Plant {
    public Tree(int x, int y, int size) {
        super(x, y, size, getRandomTreeImage());
    }

    private static String getRandomTreeImage() {
        Random rand = new Random();
        int ran = rand.nextInt(2) + 1;
        return "/images/tree" + ran + ".png";
    }
}