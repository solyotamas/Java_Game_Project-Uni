package classes.entities.human;

public class Tourist extends Human{
    private static final int frameWidth = 40;
    private static final int frameHeight = 55;
    private static double speed = 0.8;
    private static final String imgURL = "/images/animated/tourist.png";

    public Tourist(double x, double y){
        super(x, y, frameWidth, frameHeight, imgURL, speed);
    }




}

