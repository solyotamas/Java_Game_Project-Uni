package classes.landforms.plants;

import classes.landforms.Plant;

import java.util.Random;

public class Tree extends Plant {
    public Tree(double x, double y, double depth) {
        super(x, y, 2, 2, getRandomTreeImage(), depth);
    }

    private static String getRandomTreeImage() {
        Random rand = new Random();
        int ran = rand.nextInt(2) + 1;
        return "/images/tree" + ran + ".png";
    }
}