package classes.entities.human;

public class Tourist extends Human{
    private static final int frameWidth = 104;
    private static final int frameHeight = 106;
    private static double speed = 0.8;
    private static final String imgURL = "/images/animated/ranger.png";

    public Tourist(double x, double y){
        super(x, y, frameWidth, frameHeight, imgURL, speed);
    }




}

