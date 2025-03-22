package classes.placeables.animals;

import classes.placeables.Herbivore;
import classes.placeables.Placeable;

import java.util.ArrayList;

public class Elephant extends Herbivore {
    public Elephant(int x, int y) {
        super(x, y, 2, 2, "/images/elephant.png", new ArrayList<Placeable>());
    }

}
