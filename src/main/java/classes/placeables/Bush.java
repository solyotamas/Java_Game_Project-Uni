package classes.placeables;

import java.util.Random;

public class Bush extends Plant {
    public Bush(int x, int y, int size) {
        super(x, y, size, getRandomBushImage());
    }

    private static String getRandomBushImage() {
        Random rand = new Random();
        int ran = rand.nextInt(2) + 1;
        return "/images/bush" + ran + ".png";
    }
}