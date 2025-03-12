package classes.placeables.animals;

import classes.placeables.Herbivore;
import classes.placeables.Placeable;

import java.util.ArrayList;

public class Turtle extends Herbivore {
    public Turtle(int x, int y) {
        super(x, y, 60, "/images/turtle.png", new ArrayList<Placeable>());
        //a 60 az elég random
    }

}
