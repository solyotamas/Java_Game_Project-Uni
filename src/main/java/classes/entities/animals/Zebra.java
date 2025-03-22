package classes.entities.animals;

import classes.entities.Herbivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Zebra extends Herbivore {
    public Zebra(int x, int y) {
        super(x, y, 1,2, "/images/zebra.png", new ArrayList<Landform>());
    }

}
