package classes.entities.human;

import classes.entities.Direction;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Random;

public class Ranger extends Human {

    private static final int frameWidth = 36;//104;
    private static final int frameHeight = 46;//106;
    private static double speed = 0.8;
    private static final String imgURL = "/images/animated/ranger.png";
    private double lastPaidHour;


    public Ranger(double x, double y){
        super(x, y, frameWidth, frameHeight, imgURL, speed);
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

    public void setLastPaidHour(double lastPaidHour) {
        this.lastPaidHour = lastPaidHour;
    }

    public boolean isDueForPayment(double currentGameHour) {
        return currentGameHour - lastPaidHour >= 720; // 30 days * 24 hours
    }

}