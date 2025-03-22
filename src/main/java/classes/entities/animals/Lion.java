package classes.entities.animals;

import classes.entities.Carnivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Lion extends Carnivore {
    public Lion(int x, int y) {
        super(x, y, 2,1, "/images/lion.png", new ArrayList<Landform>());
    }

}
