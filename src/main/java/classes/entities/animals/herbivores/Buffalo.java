package classes.entities.animals.herbivores;

import classes.entities.animals.Herbivore;

public class Buffalo extends Herbivore {

    private static final int price = 2500;
    private static final int lifeExpectancy = 35;

    private static final int frameWidth = 104;
    private static final int frameHeight = 64;
    private static final double speed = 0.5;
    private static final String imgURL = "/images/animated/buffalo.png";

    public Buffalo(double x, double y) {
        super(x, y, frameWidth, frameHeight, imgURL, speed, price, lifeExpectancy);
    }
}
