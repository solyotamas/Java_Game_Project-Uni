package classes.entities.animals.carnivores;

import classes.entities.animals.Carnivore;

public class Panther extends Carnivore {
    //just so it looks clean
    private static final int price = 1200;
    private static final int lifeExpectancy = 25;

    private static final int frameWidth = 84;
    private static final int frameHeight = 52;
    private static final double speed = 1.3;
    private static final String imgURL = "/images/animated/panther.png";
    private static final String childImgURL = "/images/animated/panther_baby.png";

    public Panther(double x, double y, boolean isChild){
        super(x,y,frameWidth, frameHeight, childImgURL,imgURL, speed, price, lifeExpectancy, isChild);
    }
}
