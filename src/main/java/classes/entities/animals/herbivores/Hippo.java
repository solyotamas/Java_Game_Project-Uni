package classes.entities.animals.herbivores;

import classes.entities.animals.Herbivore;

public class Hippo extends Herbivore {
    //just so it looks clean
    private static final int frameWidth = 104;
    private static final int frameHeight = 106;
    private static final double speed = 0.6;
    private static final String imgURL = "/images/animated/hippo.png";

    public Hippo(double x, double y){
        super(x,y,frameWidth, frameHeight, imgURL, speed);
    }
}
