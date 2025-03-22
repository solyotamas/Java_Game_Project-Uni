package classes.placeables.animals;

import classes.placeables.Carnivore;
import classes.placeables.Placeable;

import java.util.ArrayList;

public class Lion extends Carnivore {
    public Lion(int x, int y) {
        super(x, y, 2,1, "/images/lion.png", new ArrayList<Placeable>());
    }

}
