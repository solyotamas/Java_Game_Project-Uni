package classes.landforms;

import java.util.Random;

public class Road extends Landform {
    private static final double depth = Double.MIN_VALUE + 1;

    public Road(double x, double y) {
        super(x, y, 1, 1, getRandomRoadImage(), depth);
    }

    private static String getRandomRoadImage() {
        Random rand = new Random();
        int ran = rand.nextInt(3) + 1;
        return "/images/road" + ran + ".png";
    }
}