package classes.landforms.plants;

import classes.landforms.Landform;
import javafx.scene.image.Image;

public abstract class Plant extends Landform {
    public Plant(double x, double y, int widthInTiles, int heightInTiles, Image img, double depth) {
        super(x, y, widthInTiles, heightInTiles, img, depth);
    }
}