package classes.entities.animals.carnivores;

import classes.entities.animals.Carnivore;

public class Tiger extends Carnivore {

    private static final int price = 1300;
    private static final int lifeExpectancy = 25;

    private static final int frameWidth = 78;
    private static final int frameHeight = 52;
    private static final double speed = 1.2;
    private static final String imgURL = "/images/animated/tiger.png";
    private static final String childImgURL = "/images/animated/tiger_baby.png";

    public Tiger(double x, double y, boolean isChild) {
        super(x, y, frameWidth, frameHeight, childImgURL, imgURL, speed, price, lifeExpectancy, isChild);
    }
}
