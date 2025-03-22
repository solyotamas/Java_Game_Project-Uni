package classes.entities.animals;

import classes.entities.Herbivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Turtle extends Herbivore {
    public Turtle(int x, int y) {
        super(x, y, 1,1, "/images/turtle.png", new ArrayList<Landform>());
    }

}
