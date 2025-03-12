package classes.placeables.animals;

import classes.placeables.Animal;
import classes.placeables.Placeable;

import java.util.ArrayList;

public class Lynx extends Animal {
    public Lynx(int x, int y) {
        super(x, y, 60, "/images/lynx.png", new ArrayList<Placeable>());
        //a 60 az elég random
    }
}
