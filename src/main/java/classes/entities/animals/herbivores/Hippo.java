package classes.entities.animals.herbivores;

import classes.entities.animals.Herbivore;

public class Hippo extends Herbivore {

    private static final int price = 1600;
    private static final int lifeExpectancy = 50;

    private static final int frameWidth = 96;
    private static final int frameHeight = 60;
    private static final double speed = 0.6;
    private static final String imgURL = "/images/animated/hippo.png";
    private static final String childImgURL = "/images/animated/hippo_baby.png";

    public Hippo(double x, double y, boolean isChild) {
        super(x, y, frameWidth, frameHeight, childImgURL, imgURL, speed, price, lifeExpectancy, isChild);
    }
}
