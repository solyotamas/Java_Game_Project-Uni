package classes.placeables.animals;

import classes.placeables.Herbivore;
import classes.placeables.Placeable;

import java.util.ArrayList;

public class Gazelle extends Herbivore {
    public Gazelle(int x, int y) {
        super(x, y, 1, 1, "/images/gazelle.png", new ArrayList<Placeable>());
    }
}
