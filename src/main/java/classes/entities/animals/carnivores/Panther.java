package classes.entities.animals.carnivores;

import classes.entities.animals.Carnivore;

public class Panther extends Carnivore {
    //just so it looks clean
    private static final int price = 400;
    private static final int frameWidth = 84;
    private static final int frameHeight = 72;
    private static final double speed = 1.3;
    private static final String imgURL = "/images/animated/panther.png";

    public Panther(double x, double y){
        super(x,y,frameWidth, frameHeight, imgURL, speed, price);
    }
}
