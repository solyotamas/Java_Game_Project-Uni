package classes.placeables.animals;

import classes.placeables.Carnivore;
import classes.placeables.Placeable;

import java.util.ArrayList;

public class Lynx extends Carnivore {
    public Lynx(int x, int y) {
        super(x, y, 1,1, "/images/lynx.png", new ArrayList<Placeable>());
    }
}
