package classes.terrains;

import java.util.Random;

public class Ground extends Terrain {
    public Ground(int x, int y, int size) {
        super(x, y, size, getRandomGrassImage(), 1);
    }

    private static String getRandomGrassImage() {
        Random rand = new Random();
        int ran = rand.nextInt(4) + 1;
        return "/images/grass" + ran + ".png";
    }
}