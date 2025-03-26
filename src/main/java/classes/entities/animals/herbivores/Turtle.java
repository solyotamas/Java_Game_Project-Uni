package classes.entities.animals.herbivores;

import classes.entities.animals.Herbivore;

public class Turtle extends Herbivore {

    //just so it looks clean
    private static final int frameWidth = 84;
    private static final int frameHeight = 72;
    private static final double speed = 0.2;
    private static final String imgURL = "/images/animated/turtle.png";

    public Turtle(double x, double y){
        super(x,y,frameWidth, frameHeight, imgURL, speed);
    }
}
