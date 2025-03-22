package classes.entities.animals;

import classes.entities.Carnivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Panther extends Carnivore {
    public Panther(int x, int y) {
        super(x, y, 1,1, "/images/panther.png", new ArrayList<Landform>());
    }
}
