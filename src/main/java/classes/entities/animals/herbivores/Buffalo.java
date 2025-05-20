package classes.entities.animals.herbivores;

import classes.entities.animals.Herbivore;

public class Buffalo extends Herbivore {

    private static final int price = 2500;
    private static final int lifeExpectancy = 35;

    private static final int frameWidth = 104;
    private static final int frameHeight = 64;
    private static final double speed = 0.5;
    private static final String imgURL = "/images/animated/buffalo.png";
    private static final String childImgURL = "/images/animated/buffalo_baby.png";

    public Buffalo(double x, double y, boolean isChild){
        super(x,y,frameWidth, frameHeight,childImgURL, imgURL, speed, price, lifeExpectancy, isChild);
    }
}
