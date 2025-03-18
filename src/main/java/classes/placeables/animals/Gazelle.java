package classes.placeables.animals;

import classes.placeables.Animal;
import classes.placeables.Placeable;

import java.util.ArrayList;

public class Gazelle extends Animal {

    public Gazelle(int x, int y) {
        super(x, y, 60, "/images/gazelle.png", new ArrayList<Placeable>());
    }
}
