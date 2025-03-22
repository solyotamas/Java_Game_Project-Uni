package classes.landforms;

import java.util.Random;

public class Road extends Landform {
    public Road(int x, int y) {
        super(x, y, 1, 1, getRandomRoadImage());
    }

    private static String getRandomRoadImage() {
        Random rand = new Random();
        int ran = rand.nextInt(3) + 1;
        return "/images/road" + ran + ".png";
    }
}