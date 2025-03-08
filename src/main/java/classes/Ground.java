package classes;

import java.util.Random;

public class Ground extends Tile {
    public Ground(int x, int y, int width, int height) {
        super(x, y, width, height, getRandomGrassImage());
    }

    private static String getRandomGrassImage() {
        Random rand = new Random();
        int ran = rand.nextInt(4) + 1;
        return "/images/grass" + ran + ".png";
    }
}