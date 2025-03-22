package classes.entities.animals;

import classes.entities.Carnivore;
import classes.landforms.Landform;

import java.util.ArrayList;

public class Lynx extends Carnivore {
    public Lynx(int x, int y) {
        super(x, y, 1,1, "/images/lynx.png", new ArrayList<Landform>());
    }
}
