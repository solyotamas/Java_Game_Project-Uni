package classes.entities.animals.carnivores;

import classes.entities.animals.Carnivore;

public class Lion extends Carnivore {
    //just so it looks clean
    private static final int frameWidth = 84;
    private static final int frameHeight = 72;
    private static final double speed = 0.8;
    private static final String imgURL = "/images/animated/lion.png";

    public Lion(double x, double y){
        super(x,y,frameWidth, frameHeight, imgURL, speed);
    }

}
