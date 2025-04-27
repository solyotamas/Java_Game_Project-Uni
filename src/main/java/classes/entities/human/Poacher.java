package classes.entities.human;

import classes.entities.animals.Animal;

import java.util.Random;

public class Poacher extends Human {

    private static final int frameWidth = 104;
    private static final int frameHeight = 106;
    private static final double speed = 0.4;
    private static final String imgURL = "/images/animated/ranger.png";

    public Poacher(double x, double y){
        super(x,y,frameWidth, frameHeight, imgURL, speed);
    }

    @Override
    public void pickNewTarget() {
        Random random = new Random();
        double minY = this.getImageView().getFitHeight();
        double maxY = 31 * 30;
        double minX = 5 * 30 + this.getImageView().getFitWidth() / 2;
        double maxX = 59 * 30 - this.getImageView().getFitWidth() / 2;


        this.targetX = minX + random.nextDouble() * (maxX - minX);
        this.targetY = minY + random.nextDouble() * (maxY - minY);

    }
}

