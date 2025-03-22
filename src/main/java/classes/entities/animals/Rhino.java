package classes.entities.animals;

import classes.entities.Herbivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Rhino extends Herbivore {
    public Rhino(int x, int y) {
        super(x, y, 1,1, "/images/rhino.png", new ArrayList<Landform>());
    }
}
