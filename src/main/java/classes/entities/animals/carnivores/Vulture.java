package classes.entities.animals.carnivores;

import classes.entities.animals.Carnivore;

public class Vulture extends Carnivore {
    //just so it looks clean
    private static final int price = 800;
    private static final int lifeExpectancy = 30;

    private static final int frameWidth = 64;
    private static final int frameHeight = 42;
    private static final double speed = 1.6;
    private static final String imgURL = "/images/animated/vulture.png";

    public Vulture(double x, double y){
        super(x,y,frameWidth, frameHeight, imgURL, speed, price, lifeExpectancy);
    }
}
