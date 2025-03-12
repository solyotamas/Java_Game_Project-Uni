package classes.placeables.animals;

import classes.placeables.Herbivore;
import classes.placeables.Placeable;

import java.util.ArrayList;

public class Giraffe extends Herbivore {
    public Giraffe(int x, int y) {
        super(x, y, 60, "/images/giraffe.png", new ArrayList<Placeable>());
        //a 60 az elég random
    }

}
