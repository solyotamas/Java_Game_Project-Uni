package classes.entities.animals.herbivores;

import classes.entities.animals.Herbivore;

public class Zebra extends Herbivore {

    private static final int price = 2400;
    private static final int lifeExpectancy = 20;

    private static final int frameWidth = 98;
    private static final int frameHeight = 68;
    private static final double speed = 1.0;
    private static final String imgURL = "/images/animated/zebra.png";

    public Zebra(double x, double y) {
        super(x, y, frameWidth, frameHeight, imgURL, speed, price, lifeExpectancy);
    }
}
