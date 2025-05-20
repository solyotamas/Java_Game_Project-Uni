package classes.entities.animals.carnivores;

import classes.entities.animals.Carnivore;

public class Lion extends Carnivore {

    private static final int price = 1500;
    private static final int lifeExpectancy = 20;

    private static final int frameWidth = 84;
    private static final int frameHeight = 56;
    private static final double speed = 0.8;
    private static final String imgURL = "/images/animated/lion.png";
    private static final String childImgURL = "/images/animated/lion_baby.png";

    public Lion(double x, double y, boolean isChild){
        super(x,y,frameWidth, frameHeight, childImgURL,imgURL, speed, price, lifeExpectancy, isChild);
    }

}
