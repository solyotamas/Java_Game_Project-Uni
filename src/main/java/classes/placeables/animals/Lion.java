package classes.placeables.animals;

import classes.placeables.Herbivore;
import classes.placeables.Placeable;

import java.util.ArrayList;

public class Lion extends Herbivore {
    public Lion(int x, int y) {
        super(x, y, 60, "/images/lion.png", new ArrayList<Placeable>());
        //a 60 az elég random
    }

}
