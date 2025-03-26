package classes.entities.human;

import classes.entities.animals.Animal;

public class Poacher extends Human {

    private static final int frameWidth = 104;
    private static final int frameHeight = 106;
    private static final double speed = 0.4;
    private static final String imgURL = "/images/animated/ranger.png";

    public Poacher(double x, double y){
        super(x,y,frameWidth, frameHeight, imgURL, speed);
    }

    public void captures(Animal animal) {

    }
}

