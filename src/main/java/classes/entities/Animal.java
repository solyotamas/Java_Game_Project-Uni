package classes.entities;

import classes.landforms.Landform;

import java.util.ArrayList;

public abstract class Animal extends Landform /*extends Entity*/ {
    private int age;
    //private int speed;
    private Landform target;
    private int appetite;
    private boolean hungry;
    private boolean thirsty;
    private ArrayList<Landform> consumes;
    private Herd herd;

    public Animal(int x, int y, int widthInTiles, int heightInTiles, String imgURL, ArrayList<Landform> consumes)  {
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