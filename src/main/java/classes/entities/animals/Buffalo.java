package classes.entities.animals;

import classes.entities.Herbivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Buffalo extends Herbivore {
    public Buffalo(int x, int y) {
        super(x, y, 2,2, "/images/buffalo.png", new ArrayList<Landform>());
    }

}
