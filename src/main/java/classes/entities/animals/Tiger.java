package classes.entities.animals;

import classes.entities.Carnivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Tiger extends Carnivore {
    public Tiger(int x, int y) {
        super(x, y, 1,1, "/images/tiger.png", new ArrayList<Landform>());
    }
}
