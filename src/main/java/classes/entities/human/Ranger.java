package classes.entities.human;

import classes.entities.Direction;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Ranger extends Human {

    private static final int frameWidth = 40;//104;
    private static final int frameHeight = 55;//106;
    private static double speed = 0.8;
    private static final String imgURL = "/images/animated/tourist.png";


    public Ranger(double x, double y){
        super(x, y, frameWidth, frameHeight, imgURL, speed);
    }




}