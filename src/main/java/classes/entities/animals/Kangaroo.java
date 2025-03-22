package classes.entities.animals;

import classes.entities.Herbivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Kangaroo extends Herbivore {
    public Kangaroo(int x, int y) {
        super(x, y, 1,1, "/images/kangaroo.png", new ArrayList<Landform>());
    }
}
