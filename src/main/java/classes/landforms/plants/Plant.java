package classes.landforms.plants;

import classes.landforms.Landform;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public abstract class Plant extends Landform {
    private double nutrition;
    public Plant(double x, double y, int widthInTiles, int heightInTiles, Image img, double depth, int price, int nutrition) {
        super(x, y, widthInTiles, heightInTiles, img, depth, price);
        this.nutrition = nutrition;
    }

    public void reduceNutrition(double amount) {
        nutrition -= amount;
        if (nutrition <= 0) {
            nutrition = 0;
        }
    }

    public boolean isDepleted() {
        return nutrition <= 0;
    }
}