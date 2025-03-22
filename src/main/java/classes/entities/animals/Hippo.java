package classes.entities.animals;

import classes.entities.Herbivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Hippo extends Herbivore {
    public Hippo(int x, int y) {
        super(x, y, 1, 1, "/images/hippo.png", new ArrayList<Landform>());
    }
}
