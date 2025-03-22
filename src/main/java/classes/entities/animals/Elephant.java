package classes.entities.animals;

import classes.entities.Herbivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Elephant extends Herbivore {
    public Elephant(int x, int y) {
        super(x, y, 2, 2, "/images/elephant.png", new ArrayList<Landform>());
    }

}
