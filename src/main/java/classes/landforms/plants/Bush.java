package classes.landforms.plants;

import classes.landforms.Plant;

import java.util.Random;

public class Bush extends Plant {
    public Bush(int x, int y) {
        super(x, y, 1, 1,getRandomBushImage());
    }

    private static String getRandomBushImage() {
        Random rand = new Random();
        int ran = rand.nextInt(3) + 1;
        return "/images/bush" + ran + ".png";
    }
}