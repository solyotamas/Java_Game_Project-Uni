package classes.entities.animals.herbivores;

import classes.entities.animals.Herbivore;

public class Turtle extends Herbivore {

    private static final int price = 1300;
    private static final int lifeExpectancy = 100;

    private static final int frameWidth = 63;
    private static final int frameHeight = 52;
    private static final double speed = 0.2;
    private static final String imgURL = "/images/animated/turtle.png";
    private static final String childImgURL = "/images/animated/turtle_baby.png";

    public Turtle(double x, double y, boolean isChild) {
        super(x, y, frameWidth, frameHeight, childImgURL, imgURL, speed, price, lifeExpectancy, isChild);
    }
}
