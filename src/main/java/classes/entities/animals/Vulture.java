package classes.entities.animals;

import classes.entities.Carnivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Vulture extends Carnivore {
    public Vulture(int x, int y) {
        super(x, y, 1,1, "/images/vulture.png", new ArrayList<Landform>());
    }
}
