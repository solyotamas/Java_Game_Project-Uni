package classes.placeables.animals;

import classes.placeables.Herbivore;
import classes.placeables.Placeable;

import java.util.ArrayList;

public class Kecske extends Herbivore {
    public Kecske(int x, int y) {
        super(x, y, 1,1, "/images/kecske.png", new ArrayList<Placeable>());
    }
}
