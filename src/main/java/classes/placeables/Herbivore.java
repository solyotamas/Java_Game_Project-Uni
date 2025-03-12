package classes.placeables;

import java.util.ArrayList;

public class Herbivore extends Animal {
    public Herbivore(int x, int y, int size, String imgURL, ArrayList<Placeable> consumes) {
        super(x, y, size, imgURL, consumes);
    }
}