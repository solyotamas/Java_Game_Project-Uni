package classes.entities.human;

import classes.entities.Direction;

import java.util.Random;

public class Tourist extends Human{
    private static final int frameWidth = 40;
    private static final int frameHeight = 55;
    private static double speed = 0.8;
    private static final String imgURL = "/images/animated/tourist.png";

    public Tourist(double x, double y){
        super(x, y, frameWidth, frameHeight, imgURL, speed);
    }





    @Override
    public void pickNewTarget(double x, double y) {
        Random random = new Random();
        int coinFlip = random.nextBoolean() ? 0 : 1;

        double minX = this.getImageView().getFitWidth();
        double maxX = 30 * 3 + this.getImageView().getFitWidth() / 2;
        double minY = this.getImageView().getFitHeight() / 2;
        double maxY = 30 * 31 - this.getImageView().getFitHeight() / 2;

        double randomX = minX + Math.random() * (maxX - minX);
        double randomY = minY + Math.random() * (maxY - minY);

        this.targetX = randomX;
        this.targetY = randomY;
        System.out.println(targetX + " " + targetY);
    }


}

