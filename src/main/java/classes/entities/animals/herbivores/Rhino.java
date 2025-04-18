package classes.entities.animals.herbivores;

import classes.entities.animals.Herbivore;

public class Rhino extends Herbivore {

    //just so it looks clean
    private static final int price = 400;
    private static final int frameWidth = 104;
    private static final int frameHeight = 66;
    private static final double speed = 0.5;
    private static final String imgURL = "/images/animated/rhino.png";

    public Rhino(double x, double y){
        super(x,y,frameWidth, frameHeight, imgURL, speed, price);
    }
}
