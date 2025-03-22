package classes.entities.animals;

import classes.entities.Herbivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Giraffe extends Herbivore {
    public Giraffe(int x, int y) {
        super(x, y, 2,2, "/images/giraffe.png", new ArrayList<Landform>());
    }

}
