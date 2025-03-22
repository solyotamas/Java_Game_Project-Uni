package classes.entities.animals;

import classes.entities.Herbivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Gazelle extends Herbivore {
    public Gazelle(int x, int y) {
        super(x, y, 1, 1, "/images/gazelle.png", new ArrayList<Landform>());
    }
}
