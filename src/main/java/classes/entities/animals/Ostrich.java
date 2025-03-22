package classes.entities.animals;

import classes.entities.Herbivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Ostrich extends Herbivore {
    public Ostrich(int x, int y) {
        super(x, y, 1,2, "/images/ostrich.png", new ArrayList<Landform>());
    }

}
