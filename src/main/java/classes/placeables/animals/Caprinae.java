package classes.placeables.animals;

import classes.placeables.Herbivore;
import classes.placeables.Placeable;

import java.util.ArrayList;

public class Caprinae extends Herbivore {
    public Caprinae(int x, int y) {
        super(x, y, 1,1, "/images/caprinae.png", new ArrayList<Placeable>());
    }
}
