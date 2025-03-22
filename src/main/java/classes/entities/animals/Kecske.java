package classes.entities.animals;

import classes.entities.Herbivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Kecske extends Herbivore {
    public Kecske(int x, int y) {
        super(x, y, 1,1, "/images/kecske.png", new ArrayList<Landform>());
    }
}
