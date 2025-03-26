package classes.entities.animals.carnivores;

import classes.entities.animals.Carnivore;

public class Tiger extends Carnivore {

    //just so it looks clean
    private static final int frameWidth = 84;
    private static final int frameHeight = 72;
    private static final double speed = 1.2;
    private static final String imgURL = "/images/animated/tiger.png";

    public Tiger(double x, double y){
        super(x,y,frameWidth, frameHeight, imgURL, speed);
    }
}
