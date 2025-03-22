package classes.placeables.animals;

import classes.placeables.Herbivore;
import classes.placeables.Placeable;

import java.util.ArrayList;

public class Turtle extends Herbivore {
    public Turtle(int x, int y) {
        super(x, y, 1,1, "/images/turtle.png", new ArrayList<Placeable>());
    }

}
