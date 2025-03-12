package classes.placeables;

import java.util.ArrayList;

public abstract class Animal extends Placeable {
    private int age;
    //private int speed;
    Placeable target;
    private int appetite;
    private boolean hungry;
    private boolean thirsty;
    private ArrayList<Placeable> consumes;
    private Herd herd;

    public Animal(int x, int y, int size, String imgURL, ArrayList<Placeable> consumes)  {
        super(x, y, size, imgURL);
        this.appetite = 1;
        this.consumes = consumes;
    }
}