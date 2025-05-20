package classes.entities.human;

import java.util.Random;

public class Tourist extends Human {
    private static final int frameWidth = 40;
    private static final int frameHeight = 55;
    private static double speed = 0.8;
    private static final String imgURL = "/images/animated/tourist.png";

    //left 0, right 1
    private int side;
    private double visitDuration = 0.0;

    private final Random rand = new Random();

    public Tourist(double x, double y, int side) {
        super(x, y, frameWidth, frameHeight, imgURL, speed);
        this.side = side;
    }

    @Override
    public void pickNewTarget() {
        if (visitDuration > 30.0) {
            this.transitionTo(HumanState.EXITING);
            exitSafari();
        } else {
            this.transitionTo(HumanState.MOVING);
            visitSafari();
        }

    }

    public void exitSafari() {
        if (this.side == 0) {
            targetX = this.getImageView().getFitWidth() / 2;
            targetY = 31.0 * 30 / 2. + this.getImageView().getFitHeight() / 2.;
        } else {
            targetX = 64 * 30 - this.getImageView().getFitWidth() / 2;
            targetY = 31.0 * 30 / 2. + this.getImageView().getFitHeight() / 2.;
        }
    }

    public void visitSafari() {

        double minY, maxY;
        double minX, maxX;

        if (this.side == 0) {
            minX = this.getImageView().getFitWidth() / 2;
            maxX = 30 * 3.5 + this.getImageView().getFitWidth() / 2;
            minY = this.getImageView().getFitHeight();
            maxY = 30 * 31;
        } else {
            minX = 30 * 60.5 - this.getImageView().getFitWidth() / 2;
            maxX = 30 * 64 - this.getImageView().getFitWidth() / 2;
            minY = 4 * 30 + this.getImageView().getFitHeight();
            maxY = 30 * 31;
        }

        this.targetX = minX + rand.nextDouble() * (maxX - minX);
        this.targetY = minY + rand.nextDouble() * (maxY - minY);
    }

    public void changeVisitDuration(double val) {
        this.visitDuration += val;
    }

}

