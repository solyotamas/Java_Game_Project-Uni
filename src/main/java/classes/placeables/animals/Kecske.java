package classes.placeables.animals;

import classes.placeables.Animal;
import classes.placeables.Placeable;

import java.util.ArrayList;

public class Kecske extends Animal {
    public Kecske(int x, int y) {
        super(x, y, 60, "/images/kecske.png", new ArrayList<Placeable>());
        //a 60 az elég random
    }
}
