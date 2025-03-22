package classes.placeables;

import java.util.ArrayList;

public abstract class Animal extends Placeable {
    private int age;
    //private int speed;
    private Placeable target;
    private int appetite;
    private boolean hungry;
    private boolean thirsty;
    private ArrayList<Placeable> consumes;
    private Herd herd;

    public Animal(int x, int y, int widthInTiles, int heightInTiles, String imgURL, ArrayList<Placeable> consumes)  {
        super(x, y, widthInTiles, heightInTiles, imgURL);
        this.appetite = 1;
        this.consumes = consumes;
    }

    /*
    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
        this.imageView.setX(this.x);
        this.imageView.setY(this.y);
    }*/



}