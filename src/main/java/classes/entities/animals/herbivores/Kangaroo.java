package classes.entities.animals.herbivores;

import classes.entities.animals.Herbivore;

public class Kangaroo extends Herbivore {

    private static final int price = 2000;
    private static final int lifeExpectancy = 20;

    private static final int frameWidth = 66;
    private static final int frameHeight = 78;
    private static final double speed = 1.2;
    private static final String imgURL = "/images/animated/kangaroo.png";
    private static final String childImgURL = "/images/animated/kangaroo_baby.png";

    public Kangaroo(double x, double y, boolean isChild) {
        super(x, y, frameWidth, frameHeight, childImgURL, imgURL, speed, price, lifeExpectancy, isChild);
    }
}