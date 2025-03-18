package classes.placeables.animals;

import classes.placeables.Herbivore;
import classes.placeables.Placeable;

import java.util.ArrayList;

public class Ostrich extends Herbivore {
    public Ostrich(int x, int y) {
        super(x, y, 60, "/images/ostrich.png", new ArrayList<Placeable>());
        //a 60 az elég random
    }

}
