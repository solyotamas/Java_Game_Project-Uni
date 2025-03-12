package classes.placeables.animals;

import classes.placeables.Animal;
import classes.placeables.Placeable;

import java.util.ArrayList;

public class Trestle extends Animal {
    public Trestle(int x, int y) {
        super(x, y, 60, "/images/trestle.png", new ArrayList<Placeable>());
    }
}
